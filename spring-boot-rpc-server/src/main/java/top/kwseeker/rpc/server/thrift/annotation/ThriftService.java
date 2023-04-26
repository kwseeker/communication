package top.kwseeker.rpc.server.thrift.annotation;

import org.springframework.core.annotation.AliasFor;
import top.kwseeker.rpc.server.annotation.RPCService;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@RPCService
public @interface ThriftService {

    //关联 @RPCService value
    @AliasFor(annotation = RPCService.class, attribute = "value")
    String value() default "";

    @AliasFor(annotation = RPCService.class, attribute = "value")
    String name() default "";

    @AliasFor(annotation = RPCService.class, attribute = "version")
    double version() default 1.0;
}
