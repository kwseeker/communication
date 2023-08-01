package top.kwseeker.communication.grpc.remote;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import org.junit.Test;
import top.kwseeker.communication.grpc.boot.ServiceManager;
import top.kwseeker.communication.grpc.common.v3.Command;
import top.kwseeker.communication.grpc.common.v3.Commands;
import top.kwseeker.communication.grpc.common.v3.KeyStringValuePair;
import top.kwseeker.communication.grpc.context.TracingContext;
import top.kwseeker.communication.grpc.context.tag.Tags;
import top.kwseeker.communication.grpc.context.trace.TracingSpan;
import top.kwseeker.communication.grpc.language.agent.v3.SegmentObject;
import top.kwseeker.communication.grpc.language.agent.v3.SpanLayer;
import top.kwseeker.communication.grpc.language.agent.v3.TraceSegmentReportServiceGrpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TraceSegmentServiceClientTest {

    @Test
    public void startServer() throws IOException, InterruptedException {
        TraceSegmentCollectorServer server = new TraceSegmentCollectorServer();
        server.start();
        server.blockUntilShutdown();
    }

    /**
     * 模拟从调用Controller方法（内部调用Service方法）到退出，追踪数据的创建与通过GRPC上报的过程
     */
    @Test
    public void testStartClientAndReport() throws InterruptedException {
        //创建GRPC连接，初始化生产者消费者模式组件和线程
        ServiceManager.INSTANCE.boot();

        //模拟一个请求线程追踪数据的创建和上报（EntrySpan结束时通知上报）
        TracingContext context = new TracingContext();
        //调用了Controller方法被 springmvc插件拦截
        TracingSpan controllerSpan = context.createSpan("GET:/demo/echo");
        controllerSpan.start();
        controllerSpan.setLayer(SpanLayer.Http);
        controllerSpan.tag(Tags.HTTP.METHOD, "echo");
        controllerSpan.tag("TagA", "TagAValue");        //这种方式不规范，项目中自定义Tag也需要像Tags那样统一管理自定义Tag
        //内部调用Service方法，被@TraceId插件拦截
        TracingSpan serviceSpan = context.createSpan("XxxService.someMethod(someParams,...)");
        serviceSpan.start();
        serviceSpan.setLayer(SpanLayer.Unknown);
        serviceSpan.tag("TagB", "TagBValue");
        //Service方法返回
        context.stopSpan(serviceSpan);
        //Controller方法返回
        context.stopSpan(controllerSpan);

        //关闭ServiceManager启动的组件
        Runtime.getRuntime().addShutdownHook(new Thread(ServiceManager.INSTANCE::shutdown, "skywalking service shutdown thread"));

        Thread.sleep(10000);
    }

    /**
     * 接收GRPC客户端上报的追踪数据，仅仅是打印出来
     * 默认用的双向流RPC模式
     */
    static class TraceSegmentCollectorServer {

        public static final int DEFAULT_PORT = 50051;

        private final Server server;
        private final int port;

        public TraceSegmentCollectorServer() {
            this(DEFAULT_PORT);
        }

        public TraceSegmentCollectorServer(int port) {
            this.server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                    .addService(new TraceSegmentReportServiceImpl())
                    .build();
            this.port = port;
        }

        private void start() throws IOException {
            server.start();
            System.out.println("Server started, listening on " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.out.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.out.println("*** server shut down");
            }));
        }

        private void blockUntilShutdown() throws InterruptedException {
            if (server != null) {
                server.awaitTermination();
            }
        }

        private void stop() throws InterruptedException {
            if (server != null) {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            }
        }

        static class TraceSegmentReportServiceImpl extends TraceSegmentReportServiceGrpc.TraceSegmentReportServiceImplBase {

            @Override
            public StreamObserver<SegmentObject> collect(StreamObserver<Commands> responseObserver) {
                return new StreamObserver<SegmentObject>() {
                    @Override
                    public void onNext(SegmentObject value) {
                        System.out.println("接收到客户端上报数据：" + value);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("error occur:");
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("接收完毕, 返回确认信息");
                        responseObserver.onNext(Commands.newBuilder()
                                .addCommands(Command.newBuilder()
                                        .setCommand("CommandA")
                                        .addArgs(KeyStringValuePair.newBuilder()
                                                .setKey("keyA")
                                                .setValue("valueA")
                                                .build())
                                        .build())
                                .build());
                        responseObserver.onCompleted();
                    }
                };
            }

            @Override
            public void collectInSync(SegmentObject request, StreamObserver<Commands> responseObserver) {
                //TODO
                super.collectInSync(request, responseObserver);
            }
        }
    }
}