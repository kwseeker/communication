这个模块用于展示 skywalking 是怎么借助 grpc 上报追踪数据的

核心原理：使用生产者消费者模式，Agent 通过 TraceSegmentServiceClient 在某个segment结束后，通过 afterFinished() 将追踪数据缓存到 GRPC