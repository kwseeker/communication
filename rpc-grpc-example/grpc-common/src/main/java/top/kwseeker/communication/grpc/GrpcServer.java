package top.kwseeker.communication.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.ServerCredentials;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GrpcServer {

    private int port;
    private boolean useCredential = false;
    private Map<? extends BindableService, BindableService> bindableServices = new HashMap<>();

    private Server server;
    private boolean running;

    public GrpcServer(int port) {
        this.port = port;
        ServerCredentials creds = useCredential ? InsecureServerCredentials.create() : InsecureServerCredentials.create();
        this.server = Grpc.newServerBuilderForPort(port, creds)
                .addService(bindableServices.values())
                .build();
    }

    public void registerService(BindableService bindableService) {
        bindableServices.put(bindableService.getClass(), bindableService);
    }

    protected void start() throws IOException {
        if (server == null) {
            throw new RuntimeException("Grpc server is not init");
        }
        server.start();
        System.out.println("Grpc server started, listening on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down Grpc server since JVM is shutting down");
                try {
                    GrpcServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.out.println("Grpc server stopped");
            }
        });
    }

    public boolean serverStatus() {
        return server.isTerminated();
    }

    protected void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
