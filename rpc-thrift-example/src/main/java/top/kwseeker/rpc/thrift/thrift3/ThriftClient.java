package top.kwseeker.rpc.thrift.thrift3;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;
import top.kwseeker.rpc.thrift.calculator.api.Calculator;
import top.kwseeker.rpc.thrift.greet.api.HelloWorld;

public class ThriftClient {

    public static void main(String[] args) {
        try {
            TTransport transport = new TFramedTransport(new TSocket("localhost", 8081));
            TProtocol protocol = new TCompactProtocol(transport);
            TMultiplexedProtocol mp1 = new TMultiplexedProtocol(protocol, "Calculator");
            TMultiplexedProtocol mp2 = new TMultiplexedProtocol(protocol, "HelloWorld");

            Calculator.Client calculatorClient = new Calculator.Client(mp1);
            HelloWorld.Client helloWorldClient = new HelloWorld.Client(mp2);
            transport.open();

            System.out.println(calculatorClient.add(1, 3));
            System.out.println(helloWorldClient.hello());

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }
}
