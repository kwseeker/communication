这个模块用于展示 skywalking 是怎么借助 grpc 上报追踪数据的

核心类：

ServiceManager: 这里主要是管理 TraceSegmentServiceClient、GRPCChannelManager 这两个BootService。

TraceSegmentServiceClient: 通过GRPC客户端（Stub，连接本质是GRPC Channel）上报追踪数据；
GRPCChannelManager: 管理 GRPCChannel，包括多服务节点随机连接、重连、连接状态变更通知；
GRPCChannel: 封装的Grpc Channel, 用于Channel定制化创建；
DataCarrier: 追踪数据的容器，实现了生产者消费者模式，TraceSegmentServiceClient#afterFinished()在某Segment执行完成时上报追踪数据，
    其实是先存入DataCarrier中的数据容器

核心原理：使用生产者消费者模式，Agent 通过 TraceSegmentServiceClient 在某个segment结束后，通过 afterFinished() 将追踪数据缓存到 GRPC