package top.kwseeker.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import top.kwseeker.rpc.server.annotation.EnableRPCServer;

@EnableDiscoveryClient
@EnableRPCServer
@SpringBootApplication
public class ThriftServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThriftServerApplication.class, args);
    }
}
