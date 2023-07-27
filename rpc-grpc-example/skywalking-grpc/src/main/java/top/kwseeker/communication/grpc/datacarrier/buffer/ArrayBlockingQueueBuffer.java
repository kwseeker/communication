package top.kwseeker.communication.grpc.datacarrier.buffer;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * The buffer implementation based on JDK ArrayBlockingQueue.
 * <p>
 * This implementation has better performance in server side. We are still trying to research whether this is suitable
 * for agent side, which is more sensitive about blocks.
 */
public class ArrayBlockingQueueBuffer<T> implements QueueBuffer<T> {

    private BufferStrategy strategy;
    private ArrayBlockingQueue<T> queue;
    private int bufferSize;

    ArrayBlockingQueueBuffer(int bufferSize, BufferStrategy strategy) {
        this.strategy = strategy;
        this.queue = new ArrayBlockingQueue<T>(bufferSize);
        this.bufferSize = bufferSize;
    }

    @Override
    public boolean save(T data) {
        //only BufferStrategy.BLOCKING
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            // Ignore the error
            return false;
        }
        return true;
    }

    @Override
    public void setStrategy(BufferStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void obtain(List<T> consumeList) {
        queue.drainTo(consumeList);
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }
}
