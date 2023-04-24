package top.kwseeker.rpc.client;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;

public class CglibProxyTest {

    @Test
    public void testCglibProxy() throws Exception {
        CglibProxyExample cglib = new CglibProxyExample();
        Person proxy = (Person) cglib.getProxy(Person.class);
        proxy.sayHello();
    }

    static class CglibProxyExample implements MethodInterceptor {

        public Object getProxy(Class clazz) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(this);
            return enhancer.create();
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("Before method " + method.getName());
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("After method " + method.getName());
            return result;
        }
    }

    class Person {
        public void sayHello() {
            System.out.println("Hello World!");
        }
    }
}
