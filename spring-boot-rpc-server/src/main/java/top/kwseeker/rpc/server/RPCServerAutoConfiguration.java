package top.kwseeker.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import top.kwseeker.rpc.server.annotation.RPCService;
import top.kwseeker.rpc.server.exception.RPCServerException;

/**
 *
 */
@Configuration
@ConditionalOnBean(value = "")
@EnableConfigurationProperties(RPCManagerProperties.class)
public class RPCServerAutoConfiguration implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(RPCServerAutoConfiguration.class);

    private ApplicationContext applicationContext;

    /**
     * 1 初始化 RPC Server
     */
    @Bean
    @ConditionalOnMissingBean
    public RPCServerGroup rpcServerGroup() {
        //扫描 RPC 服务
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(RPCService.class);
        if (beanNames.length == 0) {
            log.error("Can't search any thrift service annotated with @RPCService");
            throw new RPCServerException("Can not found any thrift service");
        }

        //服务包装
    }

    /**
     * 2 启动RPC Server
     */
    @Bean
    @ConditionalOnMissingBean
    public RPCServerBootstrap rpcServerBootstrap(RPCServerGroup rpcServerGroup) {
        return new RPCServerBootstrap(rpcServerGroup);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
