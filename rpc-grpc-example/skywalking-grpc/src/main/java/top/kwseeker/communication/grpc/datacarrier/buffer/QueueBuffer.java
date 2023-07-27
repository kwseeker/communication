package top.kwseeker.communication.grpc.datacarrier.buffer;

import java.util.List;

/**
 * Queue buffer interface.
 */
public interface QueueBuffer<T> {
    /**
     * Save data into the queue;
     *
     * @param data to add.
     * @return true if saved
     */
    boolean save(T data);

    /**
     * Set different strategy when queue is full.
     */
    void setStrategy(BufferStrategy strategy);

    /**
     * Obtain the existing data from the queue
     */
    void obtain(List<T> consumeList);

    int getBufferSize();
}
