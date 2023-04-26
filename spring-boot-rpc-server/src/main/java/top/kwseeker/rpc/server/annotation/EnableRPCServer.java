package top.kwseeker.rpc.server.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RPCServerConfigurationSelector.class)
public @interface EnableRPCServer {
}
