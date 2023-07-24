# GRPC

> 重新整理文档，从源码入手重学GRPC。

官方文档：

+ [grpc-java](https://grpc.io/docs/languages/java/) Java客户端

+ [grpc-web](https://grpc.io/docs/platforms/web/) 

+ [Guides](https://grpc.io/docs/guides/)

  各个功能模块的文档。包括认证、基准测试、取消RPC调用、压缩、定制后端监控指标、负载均衡策略、服务解析、超时时间、异常处理、流控、Keepalive、性能最佳实现、Wait-for-Ready。

书籍：

+ 《gRPC与云原生应用开发：以Go和Java为例》

测试代码：

主要看官方的 examples 就可以；另外也可以看一些常用中间件是怎么使用的，比如 SkyWalking-Java（这里就是看到 SkyWalking追踪数据上报这部分发现对GRPC的内容忘的差不多了又回来看GRPC的）。

> grpc-java 源码编译：
>
> 并没有配置Android环境，需要跳过Android相关的模块，另外把 examples 模块加上去。
>
> ```groovy
> ext {
>     skipAndroid = true
> }
> include ":examples"
> project(':examples').projectDir = "$rootDir/examples" as File
> ```



## 基本原理

服务端（productor）和客户端（Consumer）共同遵循 protobuf 协议（定义数据格式，不是网络通信协议）定义接口；由编译工具自动生成符合服务端和客户端语言类型的接口代码；然后服务端提供接口实现，并选择端口启动服务端（以Java为例，服务端依赖Netty服务端实现）；客户端（Stub, 当成Client就好）同样依靠生成的接口代码，实现客户端接口（以Java为例，客户端依赖Netty 客户端实现）。

+ **通信模式**

  + 普通RPC模式（Simple RPC）

    始终只有一个请求（对象）和一个响应（对象）。

    使用场景：用于请求和响应数据都比较简单的场景；纯粹的普通RPC模式只用于测试场景，因为频繁创建和关闭连接，性能差，且碰到高并发可能会出现TCP连接数占满；项目中一般会使用带连接池优化后的普通RPC模式。

    + 连接池优化的普通RPC模式

      优化点：

      + 连接池size扩缩容
      + 空闲连接超时关闭、续约
      + 池满的处理策略

      可以使用 Apache 现成的对象池实现连接池。

  + 服务器端流RPC模式（Server-side streaming RPC）

    一个请求（对象）可能有多个响应（流对象）。

    使用场景：服务端数据量较大、且数据查询时间跨度大，所以查一部分就返回一部分。

  + 客户端流RPC模式（Client-side streaming RPC）

    客户端会发送多个请求（流对象）给服务端，服务端只会发送一个响应（对象）给客户端。

    使用场景：客户端源源不断地往服务端发送数据，比如 SkyWalking 追踪数据上报就是使用的客户端流RPC模式。类似场景比如物联网IOT数据上报。

  + 双向流RPC模式（Bidirectional streaming RPC）

    客户端以消息流的形式发送请求到服务器端， 服务器端也以消息流的形式进行响应。

    使用场景：聊天应用。

+ **网络协议**

  HTTP/2

+ **传输层数据**（wireshark抓包）

+ **拦截器**

  









