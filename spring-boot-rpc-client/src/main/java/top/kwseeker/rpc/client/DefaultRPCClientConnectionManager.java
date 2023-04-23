package top.kwseeker.rpc.client;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import top.kwseeker.rpc.client.loadbalance.Address;
import top.kwseeker.rpc.client.loadbalance.DynamicServerListLoadBalancer;
import top.kwseeker.rpc.client.loadbalance.ILoadBalancer;
import top.kwseeker.rpc.client.pool.IRPCClientConnectPool;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * 只不过在连接池基础上加了ThreadLocal
 */
@Component
public class DefaultRPCClientConnectionManager implements IRPCClientConnectionManager {

    private final Logger log = LoggerFactory.getLogger(DefaultRPCClientConnectionManager.class);

    //private ThreadLocal<TSocket> socketThreadSafe = new ThreadLocal<>();
    private final ILoadBalancer loadBalancer = new DynamicServerListLoadBalancer();

    @Resource
    @Qualifier("defaultRPCClientConnectPool")
    private IRPCClientConnectPool rpcClientConnectPool;

    @Override
    public <T, R> R selectClientAndExec(RPCSupportedService service, Function<T, R> function) throws Exception {
        Address address = null;
        TSocket conn = null;
        try {
            List<Address> addresses = service.getAddresses();   //TODO 这个列表后面直接交给LoadBalancer维护
            // 1 负载均衡选择一个服务实例地址，从这个地址对应的连接池中取连接，参考Ribbon ServerListLoabBalancerTest.java
            address = loadBalancer.chooseAddress(addresses);
            log.info("chooseAddress, result address: {}", address);
            conn = rpcClientConnectPool.getConnection(address);
            TProtocol tProtocol = new TCompactProtocol(new TFramedTransport(conn, 600));    //TODO 优化
            //T client = new T(tProtocol);    //如何获取泛型反射实例化
            //Method method = function.getClass().getMethod("apply", Object.class);
            //Class<?>[] parameterTypes = method.getParameterTypes();
            //System.out.println(parameterTypes[0]);

            // 2 执行 function 远程调用
            //return function.apply(client);
            return null;
        } finally {
            // 3 归还连接
            if (address != null && conn != null) {
                rpcClientConnectPool.returnConnection(address, conn);
            }
        }
    }

    //public TSocket currentSocket() {
    //
    //}
    //
    //public TSocket getSocket() {
    //
    //}
    //
    //public void returnSocket(TSocket socket) {
    //
    //}
    //
    //public void invalidateSocket(TSocket socket) {
    //
    //}
}
