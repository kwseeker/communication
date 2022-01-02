package top.kwseeker.rpc.thrift.greet.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import top.kwseeker.rpc.thrift.greet.api.HelloWorld;
import top.kwseeker.rpc.thrift.greet.handler.HelloWorldHandler;

public class HelloWorldServer {

    public static void main(String[] args) {
        try {
            TServerTransport serverTransport = new TServerSocket(8081);

            TProcessor processor = new HelloWorld.Processor<>(new HelloWorldHandler());

            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
