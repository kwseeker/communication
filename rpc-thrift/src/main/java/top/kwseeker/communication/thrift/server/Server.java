package top.kwseeker.communication.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import top.kwseeker.communication.thrift.api.Calculator;
import top.kwseeker.communication.thrift.handler.CalculatorHandler;

/**
 * 服务端进程
 */
public class Server {

    public static void main(String[] args) {
        try {
            TServerTransport serverTransport = new TServerSocket(8081);
            TProcessor processor = new Calculator.Processor<>(new CalculatorHandler());
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
