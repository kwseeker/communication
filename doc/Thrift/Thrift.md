# Thrift

## 准备工作

+ 下载
+ 编译`Thrift Compiler`
+ 安装

+ 编写.thrift 和 RPC接口

  ```xml
  <dependency>
      <groupId>org.apache.thrift</groupId>
      <artifactId>libthrift</artifactId>
      <version>0.15.0</version>
  </dependency>
  ```

+ 使用Thrift Compiler生成Client Server代码
+ 执行测试

> 使用时只需要编写服务端RPC接口和IDL文件。

安装多个版本:

```shell
# 2021.12.31 当前最新版本 0.15.0
wget http://www.apache.org/dyn/closer.cgi?path=/thrift/0.15.0/thrift-0.15.0.tar.gz
tar zxvf thrift-0.15.0.tar.gz -C /opt/
# 安装源码编译时的依赖
sudo apt-get install automake bison flex g++ git libboost-all-dev libevent-dev libssl-dev libtool make pkg-config
# 源码编译, 详细看根目录下的README.md
#  	首次在源存储库之外构建，需要生成配置脚本
sudo ./bootstrap.sh
# 	配置
sudo ./configure
```



## 工作原理

### 架构

![](img/thrift-layers.png)

客户端/服务端 -> Thrift协议层 -> 传输协议层 -> 底层传输协议层 -> 编译语言 -> OS执行。

