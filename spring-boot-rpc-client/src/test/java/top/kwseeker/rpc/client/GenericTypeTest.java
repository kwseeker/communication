package top.kwseeker.rpc.client;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.function.Function;

public class GenericTypeTest {

    @Test
    public void testGenericTypeInstance() throws NoSuchMethodException {
        //Function<String, Integer> function = Integer::parseInt;         //匿名类型，实现 Function<T, R> 接口
        Function<String, Integer> function = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        };
        Class<?> clazz = function.getClass();
        TypeVariable<?>[] typeParameters = clazz.getTypeParameters();   //为空
        Type superClass = clazz.getGenericSuperclass();                 //Object
        //ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();  //是Object类型，没有参数化类型，不能转换
        Type[] genericInterfaces = clazz.getGenericInterfaces();        //Function, 第一种写法（lambda）没有类型信息
                                                                        //Function<String, Integer> 第二种写法有类型信息

        Method method = clazz.getMethod("apply", Object.class);
        Class<?>[] parameterTypes = method.getParameterTypes();         //方法参数类型
        for (Class<?> parameterType : parameterTypes) {
            System.out.println(parameterType);                          //Object
        }
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            System.out.println(genericParameterType);                   //Object
        }
    }

    @Test
    public void testGenericTypeInstance2() throws NoSuchMethodException {
        MyFunction function = new MyFunction();
        Class<?> clazz = function.getClass();
        TypeVariable<?>[] typeParameters = clazz.getTypeParameters();   //为空
        Type superClass = clazz.getGenericSuperclass();                 //Object
        //ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();  //是Object类型，没有参数化类型，不能转换
        Type[] genericInterfaces = clazz.getGenericInterfaces();        //Function<String, Integer> 有实际类型信息
        for (Type genericInterface : genericInterfaces) {
            Type[] actualTypeArguments = ((ParameterizedType) genericInterface).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                System.out.println(actualTypeArgument);                 //String, Integer   有实际类型信息
            }
        }

        Method method = clazz.getMethod("apply", Object.class);
        Class<?>[] parameterTypes = method.getParameterTypes();         //方法参数类型
        for (Class<?> parameterType : parameterTypes) {
            System.out.println(parameterType);                          //Object
        }
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            System.out.println(genericParameterType);                   //Object
        }
    }

    static class MyFunction implements Function<String, Integer> {
        @Override
        public Integer apply(String s) {
            return Integer.parseInt(s);
        }
    }

    @Test
    public void testGetSuperClass() {
        //ArrayList<E> extends AbstractList<E>
        //        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
        ArrayList<Integer> intList = new ArrayList<>();
        intList.add(1);
        Class<?> clazz = intList.getClass();
        Class<?> superclass = clazz.getSuperclass();            //AbstractList      获取父类
        Type genericSuperclass = clazz.getGenericSuperclass();  //AbstractList<E>   获取泛型父类
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();  //获取泛型父类的类型参数数组
        for (Type actualTypeArgument : actualTypeArguments) {
            System.out.println(actualTypeArgument);             //E     为何不是Integer
        }
        Class<?>[] interfaces = clazz.getInterfaces();          //List, RandomAccess, Cloneable, Serializable    获取接口类型(不包含泛型信息)
        Type[] genericInterfaces = clazz.getGenericInterfaces();//List<E>, RandomAccess, Cloneable, Serializable 获取接口类型(包含泛型信息)

    }

    @Test
    public void testGenericType() throws NoSuchMethodException {
        new Student();
    }

    static class Person<T> {
        public Person() {
            // 获取当前new的对象的 泛型的父类 类型
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            // 获取泛型父类类型参数的真实类型
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                System.out.println(actualTypeArgument); //class top.kwseeker.rpc.client.GenericTypeTest$Student
            }
        }
    }

    static class Student extends Person<Student> {
    }
}
