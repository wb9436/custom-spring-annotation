package com.wubing.custom.supprot.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 自定义注解
 * <p>
 * 对目标接口生成代理对象工厂
 *
 * @author: WB
 * @version: v1.0
 */
public class MyServiceProxyFactory<T> {
    private static final ClassLoader CLASS_LOADER = MyServiceProxyFactory.class.getClassLoader();

    public static <T> T getProxyInstance(Class<T> referenceInterface) {
        Object obj = Proxy.newProxyInstance(CLASS_LOADER,
                new Class[]{referenceInterface},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String referenceResult = "远程调用返回结果为：hello, world!";
                        return referenceResult;
                    }
                });
        return referenceInterface.cast(obj);
    }

}
