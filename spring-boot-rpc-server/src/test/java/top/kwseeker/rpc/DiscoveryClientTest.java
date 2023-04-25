package top.kwseeker.rpc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DiscoveryClientTest {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Test
    public void testGetServiceUrl() {
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            System.out.println(service);
        }
        //List<ServiceInstance> list = discoveryClient.getInstances("STORES");
        //for (ServiceInstance serviceInstance : list) {
        //    System.out.println(serviceInstance.getUri());
        //}
    }
}
