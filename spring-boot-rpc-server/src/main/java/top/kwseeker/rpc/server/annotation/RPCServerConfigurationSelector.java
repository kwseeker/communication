package top.kwseeker.rpc.server.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;

/**
 * TODO 原理
 */
public class RPCServerConfigurationSelector extends SpringFactoryImportSelector<EnableRPCServer> {

    private static final Logger log = LoggerFactory.getLogger(RPCServerConfigurationSelector.class);

    /**
     * 判断当前自动配置是否开启
     */
    @Override
    protected boolean isEnabled() {
        log.info("check RPCServerConfigurationSelector is enabled ...");
        //读取环境变量决定是否开启，application.yml中的配置会被自动读取到spring中保存，参考spring ioc原理
        //支持的RPC框架（当前支持Thrift GRPC）有任意一个开启开启此自动配置
        // TODO
        return false;
    }
}
