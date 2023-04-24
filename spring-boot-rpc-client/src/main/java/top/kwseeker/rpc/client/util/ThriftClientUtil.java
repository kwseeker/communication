package top.kwseeker.rpc.client.util;

import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThriftClientUtil {

    /**
     * Xxx.Client -> Xxx.Iface接口名集合
     */
    private static final ConcurrentMap<Class<?>, Set<String>> interfaceMethodCache = new ConcurrentHashMap<>();

    public static Set<String> getInterfaceMethodNames(Class<?> clientClass) {
        if (interfaceMethodCache.containsKey(clientClass)) {
            return interfaceMethodCache.get(clientClass);
        }

        Set<String> methodName = Sets.newHashSet();
        //Xxx.Client 实现 Xxx.Iface 接口
        Class<?>[] interfaces = clientClass.getInterfaces();
        for (Class<?> class1 : interfaces) {
            Method[] methods = class1.getMethods();
            for (Method method : methods) {
                methodName.add(method.getName());
            }
        }
        interfaceMethodCache.putIfAbsent(clientClass, methodName);
        return methodName;
    }
}
