package top.kwseeker.rpc.service;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import top.kwseeker.rpc.client.pool.TTSocket;
import top.kwseeker.rpc.client.pool.ThriftClientConnectPoolFactory;
import top.kwseeker.rpc.processor.thrift.user.User;
import top.kwseeker.rpc.processor.thrift.user.UserService;

import javax.annotation.Resource;

@Service("userServiceProxy")
public class UserServiceProxyImpl implements UserService.Iface {

    @Resource
    private ThriftClientConnectPoolFactory thriftClientPool;

    @Override
    public User getUser(String name) throws TException {
        TTSocket ttSocket = null;
        try {
            //通过对象池，获取一个服务客户端连接对象
            ttSocket = thriftClientPool.getConnect();
            System.out.println(ttSocket);
            System.out.println("客户端请求用户名为:" + name + "的数据");
            //调用远端对象
            User user = ttSocket.getService().getUser(name);
            System.out.println("获取成功！！！服务端返回的对象:" + user);
            //用完之后把对象还给对象池
            thriftClientPool.returnConnection(ttSocket);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常则将当前对象从池子移除
            thriftClientPool.invalidateObject(ttSocket);
        }
        return null;
    }

    @Override
    public void setUser(User user) throws TException {
        TTSocket ttSocket = null;
        try {
            //通过对象池，获取一个服务客户端连接对象
            ttSocket = thriftClientPool.getConnect();
            System.out.println(ttSocket);
            System.out.println("客户端设置用户信息为:" + user.toString());
            //调用远端对象
            ttSocket.getService().setUser(user);
            System.out.println("设置成功！！！");
            //用完之后把对象还给对象池
            thriftClientPool.returnConnection(ttSocket);
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常则将当前对象从池子移除
            thriftClientPool.invalidateObject(ttSocket);
        }
    }
}
