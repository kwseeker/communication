package top.kwseeker.rpc.client.thrift;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.rpc.client.IRPCClient;
import top.kwseeker.rpc.processor.thrift.user.UserService;

public class ThriftRpcClient implements IRPCClient {

    private static final Logger log = LoggerFactory.getLogger(ThriftRpcClient.class);

    private String host;
    private Integer port;
    private TTransport tTransport;
    private TProtocol tProtocol;
    private UserService.Client client;

    public ThriftRpcClient() {
        log.info("new ThriftClient constructed, id: {}", System.identityHashCode(this));
    }

    //private void init() throws TTransportException {
    //    tTransport = new TFramedTransport(new TSocket(host, port), 600);
    //    //协议对象 这里使用协议对象需要和服务器的一致
    //    tProtocol = new TCompactProtocol(tTransport);
    //    client = new UserService.Client(tProtocol);
    //}

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
