package top.kwseeker.rpc.client;

import org.apache.thrift.TServiceClient;

import java.util.function.Function;

public interface IRPCClientManager {

    /**
     * 根据服务信息，从连接池中选择一个连接，根据 Iface 类型实例化客户端，并将连接归还
     * @param iFaceClass    从注册中心获取的服务信息
     */
    <C extends TServiceClient> C selectProxyClient(RPCSupportedService service, Class<C> iFaceClass) throws Exception;

    /**
     * 根据服务信息，从连接池中选择一个连接，执行RPC调用获取结果，并将连接归还
     * @param service   从注册中心获取的服务信息
     * @param rpcCall   RPC调用逻辑
     */
    //<T, R> R selectClientAndExec(RPCSupportedService service, Function<T, R> rpcCall) throws Exception;
}
