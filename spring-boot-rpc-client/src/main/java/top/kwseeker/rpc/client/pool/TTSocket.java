package top.kwseeker.rpc.client.pool;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;
import top.kwseeker.rpc.processor.thrift.user.UserService;

import java.net.Socket;

public class TTSocket {

    //thrift socket对象
    private TSocket tSocket;

    // 传输对象
    private TTransport tTransport;

    // 协议对象
    private TProtocol tProtocol;

    // 服务客户端对象
    private UserService.Client client;

    /**
     * 构造方法初始化各个连接对象
     *
     * @param host server的地址
     * @param port server的端口
     */
    public TTSocket(String host, Integer port) throws TTransportException {
        tSocket = new TSocket(host, port);
        tTransport = new TFramedTransport(tSocket, 600);
        //协议对象 这里使用协议对象需要和服务器的一致
        tProtocol = new TCompactProtocol(tTransport);
        client = new UserService.Client(tProtocol);
    }

    /**
     * 获取服务客户端对象
     */
    public UserService.Client getService() {
        return client;
    }

    /**
     * 打开通道
     */
    public void open() throws TTransportException {
        if (null != tTransport && !tTransport.isOpen())
            tTransport.open();
    }

    /**
     * 关闭通道
     */
    public void close() {
        if (null != tTransport && tTransport.isOpen())
            tTransport.close();
    }

    /**
     * 判断通道是否是正常打开状态
     */
    public boolean isOpen() {
        Socket socket = tSocket.getSocket();
        return socket.isConnected() && !socket.isClosed();
    }
}
