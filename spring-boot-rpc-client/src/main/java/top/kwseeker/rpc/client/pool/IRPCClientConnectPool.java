package top.kwseeker.rpc.client.pool;

import org.apache.thrift.transport.TSocket;
import top.kwseeker.rpc.client.loadbalance.Address;

public interface IRPCClientConnectPool {

    /**
     * 在池中获取一个空闲的 TSocket 对象
     * 如果没有空闲且池子没满，就会调用工厂的 makeObject 创建一个新的对象
     * 如果满了，就会阻塞等待，直到有空闲对象或者超时
     */
    TSocket getConnection(Address address) throws Exception;

    /**
     * 将 TSocket 对象从池中移除
     */
    void invalidateConnection(Address address, TSocket socket) throws Exception;

    /**
     * 将一个用完的 TSocket 对象返还给对象池
     */
    void returnConnection(Address address, TSocket socket);
}
