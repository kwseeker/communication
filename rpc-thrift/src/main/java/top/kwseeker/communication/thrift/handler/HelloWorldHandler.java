package top.kwseeker.communication.thrift.handler;

import org.apache.thrift.TException;
import top.kwseeker.communication.thrift.api.HelloWorld;

public class HelloWorldHandler implements HelloWorld.Iface {

    @Override
    public String hello() throws TException {
        return "Hello World! From Thrift Server";
    }
}
