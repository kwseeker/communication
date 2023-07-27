package top.kwseeker.communication.grpc.datacarrier.buffer;

import top.kwseeker.communication.grpc.datacarrier.partition.IDataPartitioner;

public class Channels<T> {

    private final QueueBuffer<T>[] bufferChannels;
    private IDataPartitioner<T> dataPartitioner;
    private final BufferStrategy strategy;
    private final long size;

    public Channels(int channelSize, int bufferSize, IDataPartitioner<T> partitioner, BufferStrategy strategy) {
        this.dataPartitioner = partitioner;
        this.strategy = strategy;
        bufferChannels = new QueueBuffer[channelSize];
        for (int i = 0; i < channelSize; i++) {
            if (BufferStrategy.BLOCKING.equals(strategy)) {
                bufferChannels[i] = new ArrayBlockingQueueBuffer<>(bufferSize, strategy);
            } else {
                bufferChannels[i] = new Buffer<>(bufferSize, strategy);
            }
        }
        // noinspection PointlessArithmeticExpression
        size = 1L * channelSize * bufferSize; // it's not pointless, it prevents numeric overflow before assigning an integer to a long
    }

    public boolean save(T data) {
        int index = dataPartitioner.partition(bufferChannels.length, data);
        int retryCountDown = 1;
        if (BufferStrategy.IF_POSSIBLE.equals(strategy)) {
            int maxRetryCount = dataPartitioner.maxRetryCount();
            if (maxRetryCount > 1) {
                retryCountDown = maxRetryCount;
            }
        }
        for (; retryCountDown > 0; retryCountDown--) {
            if (bufferChannels[index].save(data)) {
                return true;
            }
        }
        return false;
    }

    public void setPartitioner(IDataPartitioner<T> dataPartitioner) {
        this.dataPartitioner = dataPartitioner;
    }

    /**
     * override the strategy at runtime. Notice, this will override several channels one by one. So, when running
     * setStrategy, each channel may use different BufferStrategy
     */
    public void setStrategy(BufferStrategy strategy) {
        for (QueueBuffer<T> buffer : bufferChannels) {
            buffer.setStrategy(strategy);
        }
    }

    /**
     * get channelSize
     */
    public int getChannelSize() {
        return this.bufferChannels.length;
    }

    public long size() {
        return size;
    }

    public QueueBuffer<T> getBuffer(int index) {
        return this.bufferChannels[index];
    }
}