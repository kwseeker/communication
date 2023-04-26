package top.kwseeker.rpc.server.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface RPCService {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";

    @AliasFor(annotation = Component.class, attribute = "value")
    String name() default "";

    double version() default 1.0;
}
