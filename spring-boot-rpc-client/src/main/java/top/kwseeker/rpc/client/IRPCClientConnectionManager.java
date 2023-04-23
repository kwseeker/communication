package top.kwseeker.rpc.client;

import java.util.function.Function;

public interface IRPCClientConnectionManager {

    /**
     * 根据服务信息，从连接池中选择一个连接，执行RPC调用获取结果，并将连接归还
     * @param service   从注册中心获取的服务信息
     * @param rpcCall   RPC调用逻辑
     */
    <T, R> R selectClientAndExec(RPCSupportedService service, Function<T, R> rpcCall) throws Exception;
}
