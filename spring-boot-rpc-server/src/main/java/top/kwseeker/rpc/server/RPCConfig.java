package top.kwseeker.rpc.server;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RPCManagerProperties.class)
//@ConditionalOnProperty(prefix = "rpc.manager", value = "enabled", havingValue = "true")
public class RPCConfig {


}
