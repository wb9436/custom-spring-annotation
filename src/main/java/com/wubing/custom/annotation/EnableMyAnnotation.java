package com.wubing.custom.annotation;

import com.wubing.custom.processor.MyAutowiredAnnotationBeanPostProcessor;
import com.wubing.custom.registrar.MyServiceScannerRegistry;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启我的自定义注解
 * <p>
 * 开启 @MyService 注解功能
 *
 * @author: WB
 * @version: v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MyServiceScannerRegistry.class, MyAutowiredAnnotationBeanPostProcessor.class})
public @interface EnableMyAnnotation {

    /**
     * 设置指定包扫描路径，默认为当前所注解的类所在包及其子包
     */
    String[] basePackages() default {};

}
