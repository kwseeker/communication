package top.kwseeker.communication.grpc.mall.mode.simple;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import top.kwseeker.communication.grpc.mall.manufacturer.Manufacturer;
import top.kwseeker.communication.grpc.mall.product.Product;
import top.kwseeker.communication.grpc.mall.product.ProductID;
import top.kwseeker.communication.grpc.mall.product.ProductServiceGrpc;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MallClient {

    private static final String DEFAULT_ADDRESS = "localhost:50051";

    private final String address;
    private final ManagedChannel channel;
    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    public MallClient() {
        this.address = DEFAULT_ADDRESS;
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        //
        // For the example we use plaintext insecure credentials to avoid needing TLS certificates. To
        // use TLS, use TlsChannelCredentials instead.
        this.channel = Grpc.newChannelBuilder(address, InsecureChannelCredentials.create())
                .build();
        this.blockingStub = ProductServiceGrpc.newBlockingStub(this.channel);
    }

    public static void main(String[] args) {
        MallClient client = new MallClient();

        Product cocaCola = Product.newBuilder()
                .setName("可口可乐")
                .setDescription("一种碳酸饮料")
                .setPrice(3.5f)
                .setManufacturer(Manufacturer.newBuilder().setId("A101").setName("美国可口可乐公司").setAddress("美国佐治亚州-亚特兰大").build())
                .build();
        ProductID cocaColaId = client.addProduct(cocaCola);

        Product pepsiCola = Product.newBuilder()
                .setName("百事可乐")
                .setDescription("另一种更甜的碳酸饮料")
                .setPrice(3.0f)
                .setManufacturer(Manufacturer.newBuilder().setId("A102").setName("美国百事可乐公司").setAddress("美国威斯特彻斯特郡").build())
                .build();
        ProductID pepsiColaId = client.addProduct(pepsiCola);

        Product product1 = client.getProduct(cocaColaId);
        System.out.println(product1);
        Product product2 = client.getProduct(pepsiColaId);
        System.out.println(product2);

        Objects.requireNonNull(client).close();
    }

    public ProductID addProduct(Product product) {
        ProductID productID = blockingStub.addProduct(product);
        System.out.println("Response productID: " + productID.toString());
        return productID;
    }

    public Product getProduct(ProductID productID) {
        Product product = blockingStub.getProduct(productID);
        System.out.println("Response product: " + product);
        return product;
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