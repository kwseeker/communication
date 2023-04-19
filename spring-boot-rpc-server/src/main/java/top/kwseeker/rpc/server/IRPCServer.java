package top.kwseeker.rpc.server;

import java.util.concurrent.CountDownLatch;

public interface IRPCServer {

    void start(CountDownLatch latch);
}
