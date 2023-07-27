package top.kwseeker.communication.grpc.datacarrier.buffer;

import top.kwseeker.communication.grpc.common.AtomicRangeInteger;

import java.util.List;

/**
 * Self implementation ring queue.
 */
public class Buffer<T> implements QueueBuffer<T> {

    private final Object[] buffer;
    private BufferStrategy strategy;
    private AtomicRangeInteger index;

    Buffer(int bufferSize, BufferStrategy strategy) {
        buffer = new Object[bufferSize];
        this.strategy = strategy;
        index = new AtomicRangeInteger(0, bufferSize);
    }

    @Override
    public void setStrategy(BufferStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public boolean save(T data) {
        int i = index.getAndIncrement();
        if (buffer[i] != null) {
            switch (strategy) {
                case IF_POSSIBLE:
                    return false;
                default:
            }
        }
        buffer[i] = data;
        return true;
    }

    @Override
    public int getBufferSize() {
        return buffer.length;
    }

    @Override
    public void obtain(List<T> consumeList) {
        this.obtain(consumeList, 0, buffer.length);
    }

    void obtain(List<T> consumeList, int start, int end) {
        for (int i = start; i < end; i++) {
            if (buffer[i] != null) {
                consumeList.add((T) buffer[i]);
                buffer[i] = null;
            }
        }
    }

}
