package top.kwseeker.rpc;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.junit.Test;
import top.kwseeker.rpc.processor.thrift.user.User;
import top.kwseeker.rpc.processor.thrift.user.UserService;

public class ThriftClientRawTest {

    @Test
    public void testThriftRawRequest() throws TException {
        TSocket socket = new TSocket("127.0.0.1", 9080);
        TTransport transport = new TFramedTransport(socket, 600);
        transport.open();
        //协议对象 这里使用协议对象需要和服务器的一致
        TProtocol protocol = new TCompactProtocol(transport);
        protocol = new TMultiplexedProtocol(protocol, UserService.class.getSimpleName());
        UserService.Client client = new UserService.Client(protocol);
        User user = client.getUser("Bob");
        if (user != null) {
            System.out.println(user);
        }
        transport.close();
    }
}
