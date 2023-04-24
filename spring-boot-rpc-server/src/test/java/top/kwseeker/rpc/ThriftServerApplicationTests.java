package top.kwseeker.rpc;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.junit.jupiter.api.Test;
import top.kwseeker.rpc.processor.thrift.user.User;
import top.kwseeker.rpc.processor.thrift.user.UserService;

class ThriftServerApplicationTests {

    @Test
    void testClientRequest() throws Exception {
        TSocket tSocket = new TSocket("127.0.0.1", 9080);
        tSocket.open();
        TTransport tTransport = new TFramedTransport(tSocket, 600);
        //协议对象 这里使用协议对象需要和服务器的一致
        TProtocol cp = new TCompactProtocol(tTransport);
        TMultiplexedProtocol mp = new TMultiplexedProtocol(cp, UserService.class.getSimpleName());

        UserService.Client client = new UserService.Client(mp);

        User user = new User();
        user.setName("Tom");
        user.setAge(25);
        client.setUser(user);

        User response = client.getUser("Tom");
        System.out.println(response.getName());
        System.out.println(response.getAge());
    }
}
