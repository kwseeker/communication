package top.kwseeker.communication.grpc.datacarrier.consumer;

import top.kwseeker.communication.grpc.datacarrier.buffer.Channels;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 固定线程数量的消费者线程组, 消费者任务对象是 TraceSegmentServiceClient (定义消费逻辑)
 * 默认启动一个线程消费五个QueueBuffer
 */
public class ConsumeDriver<T> implements IDriver {

    private boolean running;
    private ConsumerThread[] consumerThreads;
    private Channels<T> channels;
    private ReentrantLock lock;

    public ConsumeDriver(String name,
                         Channels<T> channels, Class<? extends IConsumer<T>> consumerClass,
                         int num,
                         long consumeCycle,
                         Properties properties) {
        this(channels, num);
        for (int i = 0; i < num; i++) {
            consumerThreads[i] = new ConsumerThread(
                "DataCarrier." + name + ".Consumer." + i + ".Thread", getNewConsumerInstance(consumerClass, properties),
                consumeCycle
            );
            consumerThreads[i].setDaemon(true);
        }
    }

    public ConsumeDriver(String name, Channels<T> channels, IConsumer<T> prototype, int num, long consumeCycle) {
        this(channels, num);
        prototype.init(new Properties());
        for (int i = 0; i < num; i++) {
            consumerThreads[i] = new ConsumerThread(
                "DataCarrier." + name + ".Consumer." + i + ".Thread", prototype, consumeCycle);
            consumerThreads[i].setDaemon(true);
        }
    }

    private ConsumeDriver(Channels<T> channels, int num) {
        running = false;
        this.channels = channels;
        consumerThreads = new ConsumerThread[num];
        lock = new ReentrantLock();
    }

    private IConsumer<T> getNewConsumerInstance(Class<? extends IConsumer<T>> consumerClass, Properties properties) {
        try {
            IConsumer<T> inst = consumerClass.getDeclaredConstructor().newInstance();
            inst.init(properties);
            return inst;
        } catch (InstantiationException e) {
            throw new ConsumerCannotBeCreatedException(e);
        } catch (IllegalAccessException e) {
            throw new ConsumerCannotBeCreatedException(e);
        } catch (NoSuchMethodException e) {
            throw new ConsumerCannotBeCreatedException(e);
        } catch (InvocationTargetException e) {
            throw new ConsumerCannotBeCreatedException(e);
        }
    }

    @Override
    public void begin(Channels channels) {
        if (running) {
            return;
        }
        lock.lock();
        try {
            this.allocateBuffer2Thread();
            for (ConsumerThread consumerThread : consumerThreads) {
                consumerThread.start();
            }
            running = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isRunning(Channels channels) {
        return running;
    }

    private void allocateBuffer2Thread() {
        int channelSize = this.channels.getChannelSize();
        /**
         * if consumerThreads.length < channelSize
         * each consumer will process several channels.
         *
         * if consumerThreads.length == channelSize
         * each consumer will process one channel.
         *
         * if consumerThreads.length > channelSize
         * there will be some threads do nothing.
         */
        for (int channelIndex = 0; channelIndex < channelSize; channelIndex++) {
            int consumerIndex = channelIndex % consumerThreads.length;
            consumerThreads[consumerIndex].addDataSource(channels.getBuffer(channelIndex));
        }

    }

    @Override
    public void close(Channels channels) {
        lock.lock();
        try {
            this.running = false;
            for (ConsumerThread consumerThread : consumerThreads) {
                consumerThread.shutdown();
            }
        } finally {
            lock.unlock();
        }
    }
}