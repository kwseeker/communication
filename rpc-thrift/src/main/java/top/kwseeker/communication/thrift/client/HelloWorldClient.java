package top.kwseeker.communication.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import top.kwseeker.communication.thrift.api.HelloWorld;

public class HelloWorldClient {

    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", 8081);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);

            HelloWorld.Client client = new HelloWorld.Client(protocol);

            //执行远程进程调用
            perform(client);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(HelloWorld.Client client) throws TException {
        System.out.println(client.hello());
    }
}
