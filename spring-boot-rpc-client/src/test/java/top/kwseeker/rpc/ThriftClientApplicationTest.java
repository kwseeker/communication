package top.kwseeker.rpc;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.kwseeker.rpc.processor.thrift.user.User;
import top.kwseeker.rpc.processor.thrift.user.UserService;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ThriftClientApplicationTest {

    @Resource
    UserService.Iface userServiceProxy;

    @Test
    public void testThriftRequest() throws TException {
        //User user = userServiceProxy.getUser("Bob");
        //System.out.println("thrift rpc response: " + user.toString());
        userServiceProxy.setUser(new User("Cindy", 19));
    }
}