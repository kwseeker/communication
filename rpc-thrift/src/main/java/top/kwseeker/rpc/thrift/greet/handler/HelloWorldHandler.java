package top.kwseeker.rpc.thrift.greet.handler;

import org.apache.thrift.TException;
import top.kwseeker.rpc.thrift.greet.api.HelloWorld;

public class HelloWorldHandler implements HelloWorld.Iface {

    @Override
    public String hello() throws TException {
        return "Hello World! From Thrift Server";
    }
}
