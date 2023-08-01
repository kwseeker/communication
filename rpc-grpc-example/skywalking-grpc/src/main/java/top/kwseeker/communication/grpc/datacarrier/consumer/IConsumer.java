package top.kwseeker.communication.grpc.datacarrier.consumer;

import java.util.List;
import java.util.Properties;

public interface IConsumer<T> {

    void init(final Properties properties);

    void consume(List<T> data);

    void onError(List<T> data, Throwable t);

    void onExit();

    /**
     * Notify the implementation, if there is nothing fetched from the queue. This could be used as a timer to trigger
     * reaction if the queue has no element.
     */
    default void nothingToConsume() {
        return;
    }
}