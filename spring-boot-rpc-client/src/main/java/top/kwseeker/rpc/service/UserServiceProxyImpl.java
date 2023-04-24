package top.kwseeker.rpc.service;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.kwseeker.rpc.client.DefaultRPCClientManager;
import top.kwseeker.rpc.client.RPCSupportedService;
import top.kwseeker.rpc.client.loadbalance.Address;
import top.kwseeker.rpc.processor.thrift.user.User;
import top.kwseeker.rpc.processor.thrift.user.UserService;

import javax.annotation.Resource;
import java.util.Collections;

@Service("userServiceProxy")
public class UserServiceProxyImpl implements UserService.Iface {

    private final Logger log = LoggerFactory.getLogger(UserServiceProxyImpl.class);

    @Resource
    private DefaultRPCClientManager rpcClientManager;

    @Override
    public User getUser(String name) throws TException {
        try {
            //ttSocket = thriftClientPool.getConnect();
            //log.info("客户端连接: ttSocket={}", ttSocket);
            //log.info("客户端请求: name={}", name);
            ////调用远端对象
            //User user = ttSocket.getService().getUser(name);

            //TODO 改成从服务注册中心拉取服务信息
            Address address = new Address("127.0.0.1", 9080);
            RPCSupportedService service = new RPCSupportedService();
            service.setName("UserService");
            service.setAddresses(Collections.singletonList(address));

            //从对象池线程安全地获取一个服务客户端连接对象TSocket
            UserService.Client client = rpcClientManager.selectProxyClient(service, UserService.Client.class);
            log.info("服务端请求: {}", name);
            User user = client.getUser(name);
            log.info("服务端响应: {}", user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setUser(User user) throws TException {

    }

    //@Override
    //public void setUser(User user) throws TException {
    //    TTSocket ttSocket = null;
    //    try {
    //        //通过对象池，获取一个服务客户端连接对象
    //        ttSocket = thriftClientPool.getConnect();
    //        log.info("客户端连接: ttSocket={}", ttSocket);
    //        log.info("客户端请求: user={}", user);
    //        //调用远端对象
    //        ttSocket.getService().setUser(user);
    //        log.info("客户端设置成功: user={}", user);
    //        //用完之后把对象还给对象池
    //        thriftClientPool.returnConnection(ttSocket);
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //        //出现异常则将当前对象从池子移除
    //        thriftClientPool.invalidateObject(ttSocket);
    //    }
    //}
}
