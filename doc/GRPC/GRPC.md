# GRPC

> 重新整理文档，从源码入手重学GRPC。

官方文档：

+ [grpc-java](https://grpc.io/docs/languages/java/) Java客户端

+ [grpc-web](https://grpc.io/docs/platforms/web/) 

+ [Guides](https://grpc.io/docs/guides/)

  各个功能模块的文档。包括认证、基准测试、取消RPC调用、压缩、定制后端监控指标、负载均衡策略、服务解析、超时时间、异常处理、流控、Keepalive、性能最佳实现、Wait-for-Ready。

> grpc-java 源码编译：
>
> 并没有配置Android环境，需要跳过Android相关的模块。
>
> ```groovy
> ext {
>     skipAndroid = true
> }
> ```



## 基本原理

服务端（productor）和客户端（Consumer）共同遵循 protobuf 协议（定义数据格式，不是网络通信协议）定义接口；由编译工具自动生成符合服务端和客户端语言类型的接口代码；然后服务端提供接口实现，并选择端口启动服务端（以Java为例，服务端依赖Netty服务端实现）；客户端（Stub, 当成Client就好）同样依靠生成的接口代码，实现客户端接口（以Java为例，客户端依赖Netty 客户端实现）。

+ 网络协议
+ 传输层数据（wireshark抓包）
+ 通信模式











