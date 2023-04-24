package top.kwseeker.rpc.client.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.rpc.client.loadbalance.Address;

/**
 * 创建 Thrift 客户端连接的工厂
 */
public class ThriftClientFactory extends BasePooledObjectFactory<TSocket> {

    private final Logger log = LoggerFactory.getLogger(ThriftClientFactory.class);

    private final Address address;

    public ThriftClientFactory(Address address) {
        this.address = address;
    }

    public TSocket create() throws Exception {
        log.debug("make new connection for address: {}", address.toString());

        TSocket socket = new TSocket(address.getIp(), address.getPort());
        socket.open();
        return socket;
    }

    @Override
    public PooledObject<TSocket> wrap(TSocket socket) {
        return new DefaultPooledObject<>(socket);
    }

    @Override
    public void destroyObject(PooledObject<TSocket> pooledObject) {
        //尝试关闭连接
        pooledObject.getObject().close();
    }

    /**
     * 校验对象是否可用
     * 通过 pool.setTestOnBorrow(boolean testOnBorrow) 设置
     * 设置为true这会在调用pool.borrowObject()获取对象之前调用这个方法用于校验对象是否可用
     */
    @Override
    public boolean validateObject(PooledObject<TSocket> pooledObject) {
        log.debug("validateObject");
        return pooledObject.getObject().isOpen();
    }
}