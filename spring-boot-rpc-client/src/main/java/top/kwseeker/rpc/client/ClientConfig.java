package top.kwseeker.rpc.client;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import top.kwseeker.rpc.client.pool.TTSocket;
import top.kwseeker.rpc.client.thrift.ThriftClient;
import top.kwseeker.rpc.client.pool.ThriftClientConnectPoolFactory;

@Configuration
public class ClientConfig {

    @Value("${rpc.manger.thrift.host}")
    private String host;
    @Value("${rpc.manger.thrift.port}")
    private Integer port;

    //每次请求实例化一个新的ThriftClient连接对象
    //@Bean(initMethod = "init")
    //@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    //public ThriftClient init() {
    //    System.out.println("new thrift client");
    //    ThriftClient thriftClient = new ThriftClient();
    //    thriftClient.setHost(host);
    //    thriftClient.setPort(port);
    //    return thriftClient;
    //}

    @Bean
    public ThriftClientConnectPoolFactory thriftClientPool() {
        //对象池的配置对象
        //这里测试就直接使用默认的配置
        //可以通过config 设置对应的参数
        //参数说明见  http://commons.apache.org/proper/commons-pool/api-1.6/org/apache/commons/pool/impl/GenericObjectPool.html
        GenericObjectPoolConfig<TTSocket> config = new GenericObjectPoolConfig<>();
        //创建一个池工厂对象
        return new ThriftClientConnectPoolFactory(config, host, port);
    }
}
