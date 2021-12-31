package top.kwseeker.communication.thrift;

/**
 * 服务端进程
 */
public class Server {

    //服务端接口作为RPC接口
    public int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {

    }
}
