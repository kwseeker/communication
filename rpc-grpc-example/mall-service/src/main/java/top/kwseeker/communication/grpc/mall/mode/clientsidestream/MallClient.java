package top.kwseeker.communication.grpc.mall.mode.clientsidestream;

import com.google.protobuf.Int32Value;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import top.kwseeker.communication.grpc.mall.manufacturer.Manufacturer;
import top.kwseeker.communication.grpc.mall.order.Order;
import top.kwseeker.communication.grpc.mall.order.OrderServiceGrpc;
import top.kwseeker.communication.grpc.mall.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MallClient {

    private static final String DEFAULT_ADDRESS = "localhost:50051";

    private final String address;
    private final ManagedChannel channel;
    private final OrderServiceGrpc.OrderServiceStub stub;

    public MallClient() {
        this.address = DEFAULT_ADDRESS;

        this.channel = Grpc.newChannelBuilder(address, InsecureChannelCredentials.create())
                .build();
        this.stub = OrderServiceGrpc.newStub(this.channel);
    }

    public static void main(String[] args) throws InterruptedException {
        MallClient client = new MallClient();

        Product cocaCola = Product.newBuilder()
                .setName("可口可乐")
                .setDescription("一种碳酸饮料")
                .setPrice(3.5f)
                .setManufacturer(Manufacturer.newBuilder().setId("A101").setName("美国可口可乐公司").setAddress("美国佐治亚州-亚特兰大").build())
                .build();
        Product pepsiCola = Product.newBuilder()
                .setName("百事可乐")
                .setDescription("另一种更甜的碳酸饮料")
                .setPrice(3.0f)
                .setManufacturer(Manufacturer.newBuilder().setId("A102").setName("美国百事可乐公司").setAddress("美国威斯特彻斯特郡").build())
                .build();

        Order order1 = Order.newBuilder()
                .setId("101")
                .addItems(cocaCola)
                .setDescription("订单101")
                .setTotalPrice(3.5f)
                .build();
        Order order2 = Order.newBuilder()
                .setId("102")
                .addItems(pepsiCola)
                .setDescription("订单102")
                .setTotalPrice(3.0f)
                .build();
        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        int deletedCount = client.deleteTimeoutOrder(orders);
        System.out.println("deletedCount: " + deletedCount);

        Objects.requireNonNull(client).close();
    }

    public int deleteTimeoutOrder(List<Order> orders) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] count = {0};

        StreamObserver<Int32Value> responseObserver = new StreamObserver<Int32Value>() {
            @Override
            public void onNext(Int32Value value) {
                System.out.println("onNext(): " + value.getValue());
                count[0] = value.getValue();
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError(): ");
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted()");
                latch.countDown();
            }
        };

        StreamObserver<Order> orderStreamObserver = stub.deleteTimeoutOrder(responseObserver);
        for (Order order : orders) {
            orderStreamObserver.onNext(order);
        }
        orderStreamObserver.onCompleted();

        latch.await();
        return count[0];
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
