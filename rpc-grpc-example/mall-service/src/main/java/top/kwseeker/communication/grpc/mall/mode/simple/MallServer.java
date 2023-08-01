package top.kwseeker.communication.grpc.mall.mode.simple;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import top.kwseeker.communication.grpc.mall.service.ProductServiceImpl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MallServer {

    public static final int DEFAULT_PORT = 50051;

    private final Server server;
    private final int port;

    public MallServer() {
        this(DEFAULT_PORT);
    }

    public MallServer(int port) {
        this.server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new ProductServiceImpl())
                .build();
        this.port = port;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final MallServer server = new MallServer();
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
}
