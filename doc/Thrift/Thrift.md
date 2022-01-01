# Thrift

## 简单使用

+ **安装**

  安装多个版本（指定安装目录，下面内容可放到.sh文件sudo执行）:

  ```shell
  # 2021.12.31 当前最新版本 0.15.0 os: linuxmint18
  wget https://dlcdn.apache.org/thrift/0.15.0/thrift-0.15.0.tar.gz 
  tar zxvf thrift-0.15.0.tar.gz -C /opt/
  cd /opt
  ln -s thrift-0.15.0 thrift		# 安装其他版本这里要将thrift指向新的安装包
  cd ./thrift
  # 安装源码编译时的依赖
  apt-get install automake bison flex g++ git libboost-all-dev libevent-dev libssl-dev libtool make pkg-config
  # 源码编译安装, 详细看根目录下的README.md
  #  	首次在源存储库之外构建，需要生成配置脚本
  ./bootstrap.sh
  # 	配置 detail: ./configure --help
  mkdir -p exec/bin exec/lib		# 可执行文件、头文件和依赖库会安装到/opt/thrift/exec下
  # 	  默认是/usr/local 这里放在安装包下,不然装多版本会被覆盖
  ./configure --prefix=/opt/thrift/exec
  make
  make install
  # 执行测试
  # make -k check
  # make cross
  # 添加环境变量
  sed -i '$a\export PATH=/opt/thrift/exec/bin:$PATH' /home/lee/.zshrc
  ```

+ **编写.thrift定义RPC接口**

+ **使用Thrift Compiler生成`业务接口`、`服务端业务处理器Processor`、`客户端Client`**

  使用时只需要编写IDL文件定义RPC接口，服务端实现接口并提供对应的TProcessor实例（类似Controller），客户端实例化Client并执行调用。

  ```xml
  <dependency>
      <groupId>org.apache.thrift</groupId>
      <artifactId>libthrift</artifactId>
      <version>0.15.0</version>
  </dependency>
  ```

+ **执行测试**

  

## 工作原理

### 架构

![](img/thrift-layers.png)

客户端/服务端 -> Thrift协议层 -> 传输协议层 -> 底层传输协议层 -> 编译语言 -> OS执行。

