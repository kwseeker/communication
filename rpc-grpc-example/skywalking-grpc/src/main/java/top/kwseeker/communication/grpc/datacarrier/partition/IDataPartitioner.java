package top.kwseeker.communication.grpc.datacarrier.partition;

import top.kwseeker.communication.grpc.datacarrier.buffer.BufferStrategy;

public interface IDataPartitioner<T> {

    int partition(int total, T data);

    /**
     * @return an integer represents how many times should retry when {@link BufferStrategy#IF_POSSIBLE}.
     * <p>
     * Less or equal 1, means not support retry.
     */
    int maxRetryCount();
}
