package top.kwseeker.rpc.client.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.transport.TSocket;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import top.kwseeker.rpc.client.loadbalance.Address;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("defaultRPCClientConnectPool")
public class DefaultRPCClientConnectPool implements IRPCClientConnectPool, InitializingBean, DisposableBean {

    private final GenericObjectPoolConfig<TSocket> config = new GenericObjectPoolConfig<>();
    /**
     * 基于 Commons-pool2 的 TSocket 对象池
     * 由于的连接
     * RPCType -> serviceName -> Address(ip、port) -> Pool
     */
    private final Map<Address, GenericObjectPool<TSocket>> poolMap = new ConcurrentHashMap<>();
    private final Map<Address, ThriftClientFactory> clientFactoryMap = new ConcurrentHashMap<>();

    //@Value("${}")
    //private


    @Override
    public TSocket getConnection(Address address) throws Exception {
        GenericObjectPool<TSocket> pool = poolMap.get(address);
        if (pool == null) {
            //新建连接池
            ThriftClientFactory clientFactory = clientFactoryMap.get(address);
            if (clientFactory == null) {
                clientFactory = new ThriftClientFactory(address);
                clientFactoryMap.put(address, clientFactory);
            }
            pool = initializePool(clientFactory, config);
            poolMap.put(address, pool);
        }
        return pool.borrowObject();
    }

    @Override
    public void invalidateConnection(Address address, TSocket socket) throws Exception {
        GenericObjectPool<TSocket> pool = poolMap.get(address);
        if (pool == null) {
            return;
        }
        pool.invalidateObject(socket);
        //上面两个map如果Address对应值为空也应该删除
    }

    @Override
    public void returnConnection(Address address, TSocket socket) {
        GenericObjectPool<TSocket> pool = null;
        try {
            pool = poolMap.get(address);
            if (pool == null) {
                return;
            }
            pool.returnObject(socket);
        } catch (Exception e) {
            if (pool != null && socket != null) {
                try {
                    pool.invalidateObject(socket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    protected GenericObjectPool<TSocket> initializePool(ThriftClientFactory clientFactory, GenericObjectPoolConfig<TSocket> config) {
        GenericObjectPool<TSocket> pool = new GenericObjectPool<>(clientFactory, config);
        //设置获取对象前校验对象是否可以使用
        pool.setTestOnBorrow(true);
        return pool;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

    // getter and setter --------------------------------

}
