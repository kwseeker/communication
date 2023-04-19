package top.kwseeker.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.kwseeker.rpc.server.grpc.GrpcServer;
import top.kwseeker.rpc.server.thrift.ThriftServer;

import java.util.concurrent.CountDownLatch;

@Configuration
@EnableConfigurationProperties(RPCManagerProperties.class)
public class RPCServerConfig implements SmartInitializingSingleton, ApplicationContextAware {

    private final Logger log = LoggerFactory.getLogger(RPCServerConfig.class);

    private ApplicationContext applicationContext;
    private RPCManagerProperties rpcManagerProperties;

    @Bean
    @ConditionalOnProperty(prefix = "rpc.manager.grpc", value = "enabled", havingValue = "true")
    public IRPCServer grpcServer() {
        return new GrpcServer();
    }

    @Bean
    @ConditionalOnProperty(prefix = "rpc.manager.thrift", value = "enabled", havingValue = "true")
    public IRPCServer thriftServer() {
        return new ThriftServer(rpcManagerProperties.getThrift());
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] rpcServerNames = applicationContext.getBeanNamesForType(IRPCServer.class);
        //启动所有开启的RPC Server
        CountDownLatch latch = new CountDownLatch(rpcServerNames.length);
        for (String rpcServerName : rpcServerNames) {
            log.info(">>>>>>> start rpc server: " + rpcServerName);
            IRPCServer rpcServer = applicationContext.getBean(rpcServerName, IRPCServer.class);
            Thread thread = new Thread(() -> rpcServer.start(latch), "thread-rpc-server-" + rpcServerName);
            thread.setDaemon(true);
            thread.start();
        }

        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // setter =================================================

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setRpcManagerProperties(RPCManagerProperties rpcManagerProperties) {
        this.rpcManagerProperties = rpcManagerProperties;
    }
}
