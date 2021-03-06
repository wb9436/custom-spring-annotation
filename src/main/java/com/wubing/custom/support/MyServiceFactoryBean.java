package com.wubing.custom.support;

import com.wubing.custom.factory.MyServiceProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * 自定义注解
 * <p>
 * 对目标接口创建FactoryBean
 *
 * @author: WB
 * @version: v1.0
 */
public class MyServiceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> referenceInterface;

    public MyServiceFactoryBean() {
    }

    public MyServiceFactoryBean(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public T getObject() throws Exception {
        return MyServiceProxyFactory.getProxyInstance(referenceInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return referenceInterface;
    }

    public Class<T> getReferenceInterface() {
        return referenceInterface;
    }

    public void setReferenceInterface(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }
}
