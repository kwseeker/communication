package top.kwseeker.communication.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import top.kwseeker.communication.grpc.hellogrpc.GreeterGrpc;
import top.kwseeker.communication.grpc.hellogrpc.HelloRequest;
import top.kwseeker.communication.grpc.hellogrpc.HelloResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HelloGrpcServer {

    public static final int DEFAULT_PORT = 50051;

    private final Server server;
    private final int port;

    public HelloGrpcServer() {
        this(DEFAULT_PORT);
    }

    public HelloGrpcServer(int port) {
        this.server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new GreeterImpl())
                .build();
        this.port = port;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloGrpcServer server = new HelloGrpcServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
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

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
            System.out.println("Received: " + req.toString());
            HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
