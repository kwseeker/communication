package top.kwseeker.rpc.server.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.*;

public class AnnotationTest {

    /**
     * @AliasFor 作用：
     * 1 注解中的属性可以互相为别名进行使用
     * 2 注解中使用了元注解时，可以对元注解的值进行重写，目的是为了能达到和类继承中override相似的功能
     * 3 前两种复合使用
     */
    @Test
    public void testAliasFor() {
        ChildAnnotation childAnnotation = AnnotatedElementUtils.findMergedAnnotation(MyClass.class, ChildAnnotation.class);
        BaseAnnotation baseAnnotation = AnnotatedElementUtils.findMergedAnnotation(MyClass.class, BaseAnnotation.class);
        Assert.assertEquals(1.2, childAnnotation.version(), 0.1);
        Assert.assertEquals("myClass", childAnnotation.name());
        Assert.assertEquals("myClass", baseAnnotation.value());

        ChildAnnotation childAnnotation2 = AnnotatedElementUtils.findMergedAnnotation(MyClass.class, ChildAnnotation.class);
        BaseAnnotation baseAnnotation2 = AnnotatedElementUtils.findMergedAnnotation(MyClass2.class, BaseAnnotation.class);
        Assert.assertEquals(1.2, childAnnotation2.version(), 0.1);
        Assert.assertEquals("myClass2", baseAnnotation2.value());
    }

    @ParentAnnotation(value = "myClass", version = 1.2)
    static class MyClass {
    }

    @ParentAnnotation(name = "myClass2", version = 1.2)
    static class MyClass2 {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    protected @interface BaseAnnotation {

        String value() default "";
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @BaseAnnotation
    protected @interface ChildAnnotation {
        //@AliasFor(value = "name", annotation = BaseAnnotation.class, attribute = "value") //不能这么写
        //如果想要 value 关联 @BaseAnnotation 的 value 的同时 与 本注解的 name 互为别名需要将 name 也关联到 @BaseAnnotation 的 value
        @AliasFor(annotation = BaseAnnotation.class, attribute = "value")
        String value() default "";

        @AliasFor(annotation = BaseAnnotation.class, attribute = "value")
        String name() default "";

        double version() default 1.0;
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ChildAnnotation
    protected @interface ParentAnnotation {
        //@AliasFor(value = "name", annotation = ChildAnnotation.class, attribute = "value")
        @AliasFor(annotation = ChildAnnotation.class, attribute = "value")
        String value() default "";

        //@AliasFor("value")
        @AliasFor(annotation = ChildAnnotation.class, attribute = "name")
        String name() default "";

        @AliasFor(annotation = ChildAnnotation.class, attribute = "version")
        double version() default 1.0;
    }
}
