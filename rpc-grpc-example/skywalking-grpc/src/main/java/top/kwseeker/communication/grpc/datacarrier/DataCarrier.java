package top.kwseeker.communication.grpc.datacarrier;

import top.kwseeker.communication.grpc.datacarrier.buffer.BufferStrategy;
import top.kwseeker.communication.grpc.datacarrier.buffer.Channels;
import top.kwseeker.communication.grpc.datacarrier.consumer.ConsumeDriver;
import top.kwseeker.communication.grpc.datacarrier.consumer.IConsumer;
import top.kwseeker.communication.grpc.datacarrier.consumer.IDriver;
import top.kwseeker.communication.grpc.datacarrier.partition.IDataPartitioner;
import top.kwseeker.communication.grpc.datacarrier.partition.SimpleRollingPartitioner;
import top.kwseeker.communication.grpc.util.EnvUtil;

import java.util.Properties;

import static top.kwseeker.communication.grpc.config.Config.Collector.CONSUME_CYCLE;

/**
 * 生产者消费者模型
 */
public class DataCarrier<T> {

    //容纳生产者上报的数据
    //默认：5个QueueBuffer，每个Buffer最多存300个元素，即总容量1500，上报的数据轮询分片到5个QueueBuffer
    private Channels<T> channels;
    //消费者
    private IDriver driver;
    private String name;

    public DataCarrier(int channelSize, int bufferSize) {
        this("DEFAULT", channelSize, bufferSize);
    }

    public DataCarrier(String name, int channelSize, int bufferSize) {
        this(name, name, channelSize, bufferSize);
    }

    public DataCarrier(String name, String envPrefix, int channelSize, int bufferSize) {
        this(name, envPrefix, channelSize, bufferSize, BufferStrategy.BLOCKING);
    }

    public DataCarrier(String name, String envPrefix, int channelSize, int bufferSize, BufferStrategy strategy) {
        this.name = name;
        bufferSize = EnvUtil.getInt(envPrefix + "_BUFFER_SIZE", bufferSize);
        channelSize = EnvUtil.getInt(envPrefix + "_CHANNEL_SIZE", channelSize);
        channels = new Channels<>(channelSize, bufferSize, new SimpleRollingPartitioner<T>(), strategy);
    }

    public DataCarrier(int channelSize, int bufferSize, BufferStrategy strategy) {
        this("DEFAULT", "DEFAULT", channelSize, bufferSize, strategy);
    }

    /**
     * set a new IDataPartitioner. It will cover the current one or default one.(Default is {@link
     * SimpleRollingPartitioner}
     *
     * @param dataPartitioner to partition data into different channel by some rules.
     * @return DataCarrier instance for chain
     */
    public DataCarrier setPartitioner(IDataPartitioner<T> dataPartitioner) {
        this.channels.setPartitioner(dataPartitioner);
        return this;
    }

    /**
     * produce data to buffer, using the given {@link BufferStrategy}.
     *
     * @return false means produce data failure. The data will not be consumed.
     */
    public boolean produce(T data) {
        if (driver != null) {
            if (!driver.isRunning(channels)) {
                return false;
            }
        }

        return this.channels.save(data);
    }

    /**
     * set consumeDriver to this Carrier. consumer begin to run when {@link DataCarrier#produce} begin to work.
     *
     * @param consumerClass class of consumer
     * @param num           number of consumer threads
     * @param properties    for initializing consumer.
     */
    public DataCarrier consume(Class<? extends IConsumer<T>> consumerClass,
                               int num,
                               long consumeCycle,
                               Properties properties) {
        if (driver != null) {
            driver.close(channels);
        }
        driver = new ConsumeDriver<T>(this.name, this.channels, consumerClass, num, consumeCycle, properties);
        driver.begin(channels);
        return this;
    }

    /**
     * set consumeDriver to this Carrier. consumer begin to run when {@link DataCarrier#produce} begin to work with 20
     * millis consume cycle.
     *
     * @param consumerClass class of consumer
     * @param num           number of consumer threads
     */
    public DataCarrier consume(Class<? extends IConsumer<T>> consumerClass, int num) {
        return this.consume(consumerClass, num, 20, new Properties());
    }

    /**
     * set consumeDriver to this Carrier. consumer begin to run when {@link DataCarrier#produce} begin to work.
     *
     * @param consumer single instance of consumer, all consumer threads will all use this instance.
     * @param num      consumer线程数量
     */
    public DataCarrier consume(IConsumer<T> consumer, int num, long consumeCycle) {
        if (driver != null) {
            driver.close(channels);
        }
        driver = new ConsumeDriver<T>(this.name, this.channels, consumer, num, consumeCycle);
        driver.begin(channels);
        return this;
    }

    /**
     * set consumeDriver to this Carrier. consumer begin to run when {@link DataCarrier#produce} begin to work with 20
     * millis consume cycle.
     *
     * @param consumer single instance of consumer, all consumer threads will all use this instance.
     * @param num      consumer线程数量
     */
    public DataCarrier consume(IConsumer<T> consumer, int num) {
        //return this.consume(consumer, num, 20);
        return this.consume(consumer, num, CONSUME_CYCLE);   //为方便调试，消费者线程1000ms扫描一次
    }

    ///**
    // * Set a consumer pool to manage the channels of this DataCarrier. Then consumerPool could use its own consuming
    // * model to adjust the consumer thread and throughput.
    // */
    //public DataCarrier consume(ConsumerPool consumerPool, IConsumer<T> consumer) {
    //    driver = consumerPool;
    //    consumerPool.add(this.name, channels, consumer);
    //    driver.begin(channels);
    //    return this;
    //}

    /**
     * shutdown all consumer threads, if consumer threads are running. Notice {@link BufferStrategy}: if {@link
     * BufferStrategy} == {@link BufferStrategy#BLOCKING}, shutdown consumeDriver maybe cause blocking when producing.
     * Better way to change consumeDriver are use {@link DataCarrier#consume}
     */
    public void shutdownConsumers() {
        if (driver != null) {
            driver.close(channels);
        }
    }
}
