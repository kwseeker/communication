package top.kwseeker.communication.grpc.datacarrier.partition;

/**
 * use normal int to rolling.
 */
public class SimpleRollingPartitioner<T> implements IDataPartitioner<T> {
    @SuppressWarnings("NonAtomicVolatileUpdate")
    private volatile int i = 0;

    @Override
    public int partition(int total, T data) {
        return Math.abs(i++ % total);
    }

    @Override
    public int maxRetryCount() {
        return 3;
    }
}
