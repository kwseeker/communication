package top.kwseeker.communication.grpc.datacarrier.consumer;

import top.kwseeker.communication.grpc.datacarrier.buffer.Buffer;
import top.kwseeker.communication.grpc.datacarrier.buffer.QueueBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费者线程
 */
public class ConsumerThread<T> extends Thread {

    //线程运行状态
    private volatile boolean running;
    //消费者对象，即 TraceSegmentServiceClient 对象
    private IConsumer<T> consumer;
    //此线程消费的数据源，前面说消费者线程和Channels中的QueueBuffer默认是一对一的关系，
    //这里的数据源就是QueueBuffer, 默认List中只有一个
    private List<DataSource> dataSources;
    //Buffer中没有更多数据时，线程短暂的休眠时间ms
    private long consumeCycle;

    ConsumerThread(String threadName, IConsumer<T> consumer, long consumeCycle) {
        super(threadName);
        this.consumer = consumer;
        running = false;
        dataSources = new ArrayList<DataSource>(1);
        this.consumeCycle = consumeCycle;
    }

    /**
     * add whole buffer to consume
     */
    void addDataSource(QueueBuffer<T> sourceBuffer) {
        this.dataSources.add(new DataSource(sourceBuffer));
    }

    @Override
    public void run() {
        running = true;

        final List<T> consumeList = new ArrayList<T>(1500);
        while (running) {

            if (!consume(consumeList)) {
                try {
                    Thread.sleep(consumeCycle);
                } catch (InterruptedException e) {
                }
            }
        }

        // consumer thread is going to stop
        // consume the last time
        consume(consumeList);

        consumer.onExit();
    }


    private boolean consume(List<T> consumeList) {
        for (DataSource dataSource : dataSources) {
            dataSource.obtain(consumeList);
        }

        if (!consumeList.isEmpty()) {
            try {
                System.out.println("Consumer thread consume size: " + consumeList.size());
                consumer.consume(consumeList);
            } catch (Throwable t) {
                consumer.onError(consumeList, t);
            } finally {
                consumeList.clear();
            }
            return true;
        }
        consumer.nothingToConsume();
        return false;
    }

    void shutdown() {
        running = false;
    }

    /**
     * DataSource is a reference to {@link Buffer}.
     */
    class DataSource {
        private QueueBuffer<T> sourceBuffer;

        DataSource(QueueBuffer<T> sourceBuffer) {
            this.sourceBuffer = sourceBuffer;
        }

        void obtain(List<T> consumeList) {
            sourceBuffer.obtain(consumeList);
        }
    }
}
