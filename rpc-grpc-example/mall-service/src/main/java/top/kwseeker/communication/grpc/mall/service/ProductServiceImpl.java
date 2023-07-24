package top.kwseeker.communication.grpc.mall.service;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import top.kwseeker.communication.grpc.mall.product.Product;
import top.kwseeker.communication.grpc.mall.product.ProductID;
import top.kwseeker.communication.grpc.mall.product.ProductServiceGrpc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final Map<String, Product> productMap = new HashMap<>();

    @Override
    public void addProduct(Product request, StreamObserver<ProductID> responseObserver) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        request = request.toBuilder().setId(uuid).build();
        productMap.put(uuid, request);

        ProductID productID = ProductID.newBuilder().setValue(uuid).build();
        responseObserver.onNext(productID);
        responseObserver.onCompleted();
    }

    @Override
    public void getProduct(ProductID request, StreamObserver<Product> responseObserver) {
        String productId = request.getValue();
        Product product = productMap.get(productId);
        if (product != null) {
            responseObserver.onNext(product);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new StatusException(Status.NOT_FOUND));
        }
    }
}
