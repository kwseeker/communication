package top.kwseeker.communication.grpc;

import io.grpc.*;
import top.kwseeker.communication.grpc.hellogrpc.GreeterGrpc;
import top.kwseeker.communication.grpc.hellogrpc.HelloRequest;
import top.kwseeker.communication.grpc.hellogrpc.HelloResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HelloGrpcClient {

    private static final String DEFAULT_ADDRESS = "localhost:50051";

    private final String address;
    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloGrpcClient() {
        this.address = DEFAULT_ADDRESS;
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        //
        // For the example we use plaintext insecure credentials to avoid needing TLS certificates. To
        // use TLS, use TlsChannelCredentials instead.
        this.channel = Grpc.newChannelBuilder(address, InsecureChannelCredentials.create())
                .build();
        this.blockingStub = GreeterGrpc.newBlockingStub(this.channel);
    }

    public static void main(String[] args) {
        String user = "grpc";

        HelloGrpcClient client = null;
        try {
            client = new HelloGrpcClient();
            client.greet(user);
        } finally {
            Objects.requireNonNull(client).close();
        }
    }

    public void greet(String name) {
        System.out.println("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return;
        }
        System.out.println("Response Greeting: " + response.getMessage());
    }

    public void close() {
        try {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}