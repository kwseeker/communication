package top.kwseeker.communication.grpc.remote;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class GRPCChannelManagerTest {

    @Test
    public void testDNSResolve() throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName("localhost");
        for (InetAddress address : addresses) {
            System.out.println(address.getHostAddress());
        }
    }
}