package top.kwseeker.communication.grpc.remote;

import io.grpc.Channel;
import top.kwseeker.communication.grpc.boot.BootService;
import top.kwseeker.communication.grpc.boot.DefaultNamedThreadFactory;
import top.kwseeker.communication.grpc.config.Config;
import top.kwseeker.communication.grpc.remote.builder.AgentIDDecorator;
import top.kwseeker.communication.grpc.remote.builder.AuthenticationDecorator;
import top.kwseeker.communication.grpc.remote.builder.StandardChannelBuilder;
import top.kwseeker.communication.grpc.util.RunnableWithExceptionProtection;
import top.kwseeker.communication.grpc.util.StringUtil;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.skywalking.apm.agent.core.conf.Config.Collector.IS_RESOLVE_DNS_PERIODICALLY;

/**
 * Channel是真正的连接对象，GRPCChannelManager 连接管理器
 * 功能：
 * 1 启动阶段创建一个定时任务，这个定时任务根据 Config.Collector.BACKEND_SERVICE 配置的地址随机选择一个服务节点创建GRPC连接
 */
public class GRPCChannelManager implements BootService, Runnable {

    private volatile GRPCChannel managedChannel = null;
    private volatile ScheduledFuture<?> connectCheckFuture;
    //是否需要重连的意思
    private volatile boolean reconnect = true;
    //用于随机从多个服务节点中选择一个节点连接
    private final Random random = new Random();
    //关注连接状态的Listener
    private final List<GRPCChannelListener> listeners = Collections.synchronizedList(new LinkedList<>());
    //Collector服务地址列表，多节点
    private volatile List<String> grpcServers;
    //被选中连接的节点索引
    private volatile int selectedIdx = -1;
    //需要重连时触发重连的周期次数，连接成功后清零，每个周期默认30秒，Config.Agent.FORCE_RECONNECTION_PERIOD 如果为1，就会每30秒重连一次，如果为2就是2*30秒重连一次
    private volatile int reconnectCount = 0;

    @Override
    public void prepare() throws Throwable {
        //do nothing
    }

    @Override
    public void boot() throws Throwable {
        if (Config.Collector.BACKEND_SERVICE.trim().length() == 0) {
            System.err.println("Collector server addresses are not set.");
            System.err.println("Agent will not uplink any data.");
            return;
        }
        grpcServers = Arrays.asList(Config.Collector.BACKEND_SERVICE.split(","));
        connectCheckFuture = Executors.newSingleThreadScheduledExecutor(
                new DefaultNamedThreadFactory("GRPCChannelManager")
        ).scheduleAtFixedRate(
                new RunnableWithExceptionProtection(
                        this,
                        t -> {
                            System.err.println("unexpected exception: ");
                            t.printStackTrace();
                        }
                ), 0, Config.Collector.GRPC_CHANNEL_CHECK_INTERVAL, TimeUnit.SECONDS
        );
    }

    @Override
    public void onComplete() throws Throwable {
        //do nothing
    }

    @Override
    public void shutdown() throws Throwable {
        if (connectCheckFuture != null) {
            connectCheckFuture.cancel(true);
        }
        if (managedChannel != null) {
            managedChannel.shutdownNow();
        }
        System.out.println("Selected collector grpc service shutdown.");
    }

    @Override
    public void run() {
        System.out.println("Selected collector grpc service running, reconnect: " + reconnect);
        //是否启用定期解析DNS以更新接收方服务地址 && 是否需要重连
        if (IS_RESOLVE_DNS_PERIODICALLY && reconnect) {
            grpcServers = Arrays.stream(Config.Collector.BACKEND_SERVICE.split(","))
                    .filter(StringUtil::isNotBlank)
                    .map(eachBackendService -> eachBackendService.split(":"))
                    .filter(domainPortPairs -> {
                        if (domainPortPairs.length < 2) {
                            System.out.println("Service address " + domainPortPairs[0] + " format error. The expected format is IP:port");
                            return false;
                        }
                        return true;
                    })
                    .flatMap(domainPortPairs -> {
                        try {
                            return Arrays.stream(InetAddress.getAllByName(domainPortPairs[0]))
                                    //host 解析为 ip
                                    .map(InetAddress::getHostAddress)
                                    .map(ip -> String.format("%s:%s", ip, domainPortPairs[1]));
                        } catch (Throwable t) {
                            System.out.println("Failed to resolve " + domainPortPairs[0] + " of backend service.");
                            t.printStackTrace();
                        }
                        return Stream.empty();
                    })
                    .distinct()
                    .collect(Collectors.toList());
        }

        if (reconnect) {
            if (grpcServers.size() > 0) {
                String server = "";
                try {
                    //即随机选择一个Collector服务节点
                    int index = Math.abs(random.nextInt()) % grpcServers.size();
                    if (index != selectedIdx) {
                        selectedIdx = index;

                        server = grpcServers.get(index);
                        String[] ipAndPort = server.split(":");

                        if (managedChannel != null) {
                            managedChannel.shutdownNow();
                        }

                        //TODO
                        managedChannel = GRPCChannel.newBuilder(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                                .addManagedChannelBuilder(new StandardChannelBuilder())
                                //.addManagedChannelBuilder(new TLSChannelBuilder())
                                .addChannelDecorator(new AgentIDDecorator())
                                .addChannelDecorator(new AuthenticationDecorator())
                                .build();

                        reconnectCount = 0;
                        reconnect = false;
                        //通知 TraceSegmentServiceClient 可以上报了
                        notify(GRPCChannelStatus.CONNECTED);
                    } else if (managedChannel.isConnected(++reconnectCount > Config.Agent.FORCE_RECONNECTION_PERIOD)) {
                        // Reconnect to the same server is automatically done by GRPC,
                        // therefore we are responsible to check the connectivity and
                        // set the state and notify listeners
                        reconnectCount = 0;
                        reconnect = false;
                        notify(GRPCChannelStatus.CONNECTED);
                    }

                    return;
                } catch (Throwable t) {
                    System.out.println("Create channel to " + server + " fail.");
                    t.printStackTrace();
                }
            }

            System.out.println(
                    "Selected collector grpc service is not available. Wait " + Config.Collector.GRPC_CHANNEL_CHECK_INTERVAL + " seconds to retry");
        }
    }

    public void addChannelListener(GRPCChannelListener listener) {
        listeners.add(listener);
    }

    public Channel getChannel() {
        return managedChannel.getChannel();
    }

    private void notify(GRPCChannelStatus status) {
        for (GRPCChannelListener listener : listeners) {
            try {
                listener.statusChanged(status);
            } catch (Throwable t) {
                System.err.println("Fail to notify " + listener.getClass().getName() + " about channel connected.");
                t.printStackTrace();
            }
        }
    }
}
