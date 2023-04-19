package top.kwseeker.rpc.client.thrift;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;
import top.kwseeker.rpc.processor.thrift.user.UserService;

public class ThriftClient {

    private String host;
    private Integer port;
    private TTransport tTransport;
    private TProtocol tProtocol;
    private UserService.Client client;

    public ThriftClient() {
        System.out.println("ThriftClient constructor");
    }

    private void init() throws TTransportException {
        tTransport = new TFramedTransport(new TSocket(host, port), 600);
        //协议对象 这里使用协议对象需要和服务器的一致
        tProtocol = new TCompactProtocol(tTransport);
        client = new UserService.Client(tProtocol);
    }

    public UserService.Client getService() {
        return client;
    }

    public void open() throws TTransportException {
        if (null != tTransport && !tTransport.isOpen())
            tTransport.open();
    }

    public void close() {
        if (null != tTransport && tTransport.isOpen())
            tTransport.close();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
