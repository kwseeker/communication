package top.kwseeker.rpc.client;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.function.Function;

public class GenericTypeTest {

    @Test
    public void testGenericTypeInstance() throws NoSuchMethodException {
        Function<String, Integer> function = Integer::parseInt;
        Method method = function.getClass().getMethod("apply", Object.class);
        Class<?>[] parameterTypes = method.getParameterTypes();
        System.out.println(parameterTypes[0]);  // 输出 "class java.lang.Object"
    }
}
