package top.kwseeker.communication.grpc.mall.service;

import com.google.protobuf.Int32Value;
import io.grpc.stub.StreamObserver;
import top.kwseeker.communication.grpc.mall.order.Order;
import top.kwseeker.communication.grpc.mall.order.OrderServiceGrpc;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    /**
     * 客户端流模式接口
     */
    @Override
    public StreamObserver<Order> deleteTimeoutOrder(StreamObserver<Int32Value> responseObserver) {
        System.out.println("server call deleteTimeoutOrder()");
        //return super.deleteTimeoutOrder(responseObserver);
        return new StreamObserver<Order>() {
            final AtomicInteger count = new AtomicInteger(0);

            @Override
            public void onNext(Order value) {
                System.out.println("onNext():"  + Thread.currentThread().getName() + ", delete order: " + value.toString());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count.incrementAndGet();
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError(): ");
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted()");
                Int32Value value = Int32Value.newBuilder().setValue(count.get()).build();
                responseObserver.onNext(value);
                responseObserver.onCompleted();
            }
        };
    }
}
