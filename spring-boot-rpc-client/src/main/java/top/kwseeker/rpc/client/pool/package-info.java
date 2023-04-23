package top.kwseeker.rpc.client.pool;

/*
* 连接池实现
*
* 连接 TSocket (不只这一种通信方式，暂时只讨论TSocket) 只与 ip port 绑定
* 和 通信协议 以及 服务端接口 无关
*
* 所以连接池只管理纯粹的 TSocket 对象
* */