package top.kwseeker.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

/**
 * RPC Server 启动器，管理 RPC Server 生命周期
 * TODO Spring SmartLifecycle 处理流程
 */
public class RPCServerBootstrap implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(RPCServerBootstrap.class);

    private final RPCServerGroup rpcServerGroup;


    public RPCServerBootstrap(RPCServerGroup rpcServerGroup) {
        this.rpcServerGroup = rpcServerGroup;
    }

    @Override
    public boolean isAutoStartup() {
        boolean autoStartup = true;
        log.info("isAutoStartup: {}", autoStartup);
        return autoStartup;
    }

    //用于控制启动顺序
    @Override
    public int getPhase() {
        //最后一个启动
        return Integer.MAX_VALUE;
    }

    @Override
    public void start() {
        log.info("Starting RPC Server ...");
        rpcServerGroup.startAllServers();
    }

    @Override
    public void stop() {
        log.info("Stopping RPC Server ...");
        if (isRunning()) {
            log.info("Shutting down RPC Server ...");
            rpcServerGroup.stopAllServers();
        } else {
            log.info("RPC Server already stopped");
        }
    }

    //@Override
    //public void stop(Runnable callback) {
    //}

    @Override
    public boolean isRunning() {
        log.info("Check RPC Server is running ...");
        return rpcServerGroup.hasRunningServers();
    }
}
