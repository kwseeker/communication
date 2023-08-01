package top.kwseeker.communication.grpc.remote;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import top.kwseeker.communication.grpc.boot.BootService;
import top.kwseeker.communication.grpc.boot.ServiceManager;
import top.kwseeker.communication.grpc.common.v3.Commands;
import top.kwseeker.communication.grpc.config.Config;
import top.kwseeker.communication.grpc.context.TracingContext;
import top.kwseeker.communication.grpc.context.TracingContextListener;
import top.kwseeker.communication.grpc.context.trace.TraceSegment;
import top.kwseeker.communication.grpc.datacarrier.DataCarrier;
import top.kwseeker.communication.grpc.datacarrier.buffer.BufferStrategy;
import top.kwseeker.communication.grpc.datacarrier.consumer.IConsumer;
import top.kwseeker.communication.grpc.language.agent.v3.SegmentObject;
import top.kwseeker.communication.grpc.language.agent.v3.TraceSegmentReportServiceGrpc;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static top.kwseeker.communication.grpc.config.Config.Buffer.BUFFER_SIZE;
import static top.kwseeker.communication.grpc.config.Config.Buffer.CHANNEL_SIZE;
import static top.kwseeker.communication.grpc.remote.GRPCChannelStatus.CONNECTED;

/**
 * 用于上报追踪信息的 GRPC 客户端
 *  兼任 GRPC客户端、上报数据的容器、消费者 这3种角色
 * 基于生产者消费者模式
 * 启动阶段：
 * 1 注册Channel状态变更监听器, 监听 GRPCChannelManager 创建的连接的状态（真正的通信通道）, 如果某个Channel连接建立创建客户端Stub
 * 2 建立连接
 */
public class TraceSegmentServiceClient implements BootService, GRPCChannelListener,
        IConsumer<TraceSegment>, TracingContextListener {

    //上报的segment数量计数器
    private long segmentUplinkedCounter;
    //被抛弃的segment数量计数器，默认有上报上限
    private long segmentAbandonedCounter;
    //实现生产者消费者模式的容器
    private volatile DataCarrier<TraceSegment> carrier;
    //自动生成的Stub, 支持异步RPC调用, 只要是使用客户端流RPC模式
    private volatile TraceSegmentReportServiceGrpc.TraceSegmentReportServiceStub serviceStub;
    private volatile GRPCChannelStatus status = GRPCChannelStatus.DISCONNECT;

    @Override
    public void prepare() throws Throwable {
        ServiceManager.INSTANCE.findService(GRPCChannelManager.class).addChannelListener(this);
    }

    @Override
    public void boot() throws Throwable {
        segmentUplinkedCounter = 0;
        segmentAbandonedCounter = 0;
        carrier = new DataCarrier<>(CHANNEL_SIZE, BUFFER_SIZE, BufferStrategy.IF_POSSIBLE);
        //其实就是启动消费者线程，num=1, 即只启动一个消费者线程
        carrier.consume(this, 1);
    }

    @Override
    public void onComplete() throws Throwable {
        TracingContext.ListenerManager.add(this);
    }

    @Override
    public void shutdown() throws Throwable {
        TracingContext.ListenerManager.remove(this);
        carrier.shutdownConsumers();
    }

    @Override
    public void init(final Properties properties) {
    }

    //ConsumerThread循环查看Buffer是否为空，不为空则调用此方法上报，如果Buffer为空则会等20ms再查看，然后循环
    @Override
    public void consume(List<TraceSegment> data) {
        if (CONNECTED.equals(status)) {
            final GRPCStreamServiceStatus status = new GRPCStreamServiceStatus(false);
            //
            StreamObserver<SegmentObject> upstreamSegmentStreamObserver = serviceStub
                    //
                    .withDeadlineAfter(Config.Collector.GRPC_UPSTREAM_TIMEOUT, TimeUnit.SECONDS)
                    //
                    .collect(new StreamObserver<Commands>() {
                        @Override
                        public void onNext(Commands commands) {
                            //ServiceManager.INSTANCE.findService(CommandService.class)
                            //        .receiveCommand(commands);
                            System.out.println("接收到服务端的确认信息：" + commands);
                        }

                        @Override
                        public void onError(
                                Throwable throwable) {
                            status.finished();
                            System.err.println("Send UpstreamSegment to collector fail with a grpc internal exception.");
                            throwable.printStackTrace();
                            //如果是网络异常，更新 GRPCChannelStatus 为 DISCONNECT
                            //ServiceManager.INSTANCE
                            //        .findService(GRPCChannelManager.class)
                            //        .reportError(throwable);
                        }

                        @Override
                        public void onCompleted() {
                            status.finished();
                        }
                    });

            try {
                //以流的方式上报
                for (TraceSegment segment : data) {
                    SegmentObject upstreamSegment = segment.transform();
                    upstreamSegmentStreamObserver.onNext(upstreamSegment);
                }
            } catch (Throwable t) {
                System.err.println("Transform and send UpstreamSegment to collector fail.");
                t.printStackTrace();
            }

            upstreamSegmentStreamObserver.onCompleted();

            status.wait4Finish();
            segmentUplinkedCounter += data.size();
        } else {
            segmentAbandonedCounter += data.size();
        }
    }

    /**
     * 消费异常处理
     */
    @Override
    public void onError(List<TraceSegment> data, Throwable t) {
        System.err.println("Try to send " + data.size() + " trace segments to collector, with unexpected exception.");
    }

    @Override
    public void onExit() {
    }

    /**
     * Segment结束（EntrySpan结束）
     */
    @Override
    public void afterFinished(TraceSegment traceSegment) {
        if (!carrier.produce(traceSegment)) {
            System.err.println("One trace segment has been abandoned, cause by buffer is full.");
        }
    }

    @Override
    public void statusChanged(GRPCChannelStatus status) {
        if (CONNECTED.equals(status)) {
            Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
            serviceStub = TraceSegmentReportServiceGrpc.newStub(channel);
        }
        this.status = status;
    }
}
