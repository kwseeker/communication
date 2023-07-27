package top.kwseeker.communication.grpc.remote;

import io.grpc.Channel;
import top.kwseeker.communication.grpc.boot.BootService;
import top.kwseeker.communication.grpc.boot.ServiceManager;
import top.kwseeker.communication.grpc.context.trace.TraceSegment;
import top.kwseeker.communication.grpc.datacarrier.DataCarrier;
import top.kwseeker.communication.grpc.language.agent.v3.TraceSegmentReportServiceGrpc;

import java.util.List;

import static org.apache.skywalking.apm.agent.core.remote.GRPCChannelStatus.CONNECTED;
import static top.kwseeker.communication.grpc.config.Config.Buffer.BUFFER_SIZE;
import static top.kwseeker.communication.grpc.config.Config.Buffer.CHANNEL_SIZE;

/**
 * 用于上报追踪信息的 GRPC 客户端
 * 基于生产者消费者模式
 * 启动阶段：
 * 1 注册Channel状态变更监听器, 监听 GRPCChannelManager 创建的连接的状态（真正的通信通道）, 如果某个Channel连接建立创建客户端Stub
 * 2 建立连接
 */
public class TraceSegmentServiceClient implements BootService, GRPCChannelListener {

    private long segmentUplinkedCounter;
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
    public void consume(List<org.apache.skywalking.apm.agent.core.context.trace.TraceSegment> data) {

    }

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
