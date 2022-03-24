package com.wubing.custom.support;

import com.wubing.custom.annotation.EnableMyAnnotation;
import com.wubing.custom.annotation.MyAutowired;
import com.wubing.custom.annotation.MyService;
import com.wubing.custom.util.BeanUtils;
import com.wubing.custom.util.PathConverter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义注解
 * <p>
 * 扫描@MyService注解，并将被注解的对象注册到Spring容器中
 *
 * @author: WB
 * @version: v1.0
 */
public class MyServiceScannerRegistry implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableMyAnnotation.class.getName()));
        if (annotationAttributes != null) {
            List<String> basePackages = new ArrayList<>();

            String[] packages = annotationAttributes.getStringArray("basePackages");
            for (String aPackage : packages) {
                //判断是否为空
                if (StringUtils.hasText(aPackage)) {
                    basePackages.add(aPackage);
                }
            }
            //如果未指定basePackages属性值，则为被标注类所在的包及其子包
            if (basePackages.isEmpty()) {
                String aPackage = BeanUtils.getPackage(annotationMetadata.getClassName());
                basePackages.add(aPackage);
            }
            for (String basePackage : basePackages) {
                try {
                    registerCustomAnnotationBean(basePackage, registry);
                } catch (Exception e) {
                    throw new IllegalArgumentException("custom annotation scanner error", e);
                }
            }
        }
    }

    /**
     * 将自定义注解标注的Bean注册到Spring容器中
     *
     * @param basePackage 包路径
     * @param registry    BeanDefinition注册器
     */
    private void registerCustomAnnotationBean(String basePackage, BeanDefinitionRegistry registry) throws Exception {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        //定义路径匹配解析器
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        //将：com.wubing -> com/wubing/**/*.class
        String packageSearchPath = PathConverter.converter(basePackage);
        //获取匹配路径下的资源
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        //读取资源元数据添加到集合中
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                //将目标类封装成BeanDefinition
                ScannedGenericBeanDefinition candidate = new ScannedGenericBeanDefinition(metadataReader);
                candidate.setResource(resource);
                candidate.setSource(resource);
                candidates.add(candidate);
            }
        }
        //遍历解析指定包路径下的所有类
        for (BeanDefinition candidate : candidates) {
            String className = candidate.getBeanClassName();

            Class<?> beanClass = Class.forName(className);
            //注册带有@MyService的Bean
            MyService annotation = beanClass.getAnnotation(MyService.class);
            if (annotation != null) {
                //@MyService只能标注在接口上
                if (!beanClass.isInterface()) {
                    throw new IllegalArgumentException(beanClass.getName() + " cannot annotated @MyService annotation, it is only can annotated on interface class");
                }
                //将@MyService 标注的接口的FactoryBean注入Spring容器
                this.registryProxyFactoryBean(beanClass, registry);
            }
            //处理@MyAutowired注解的属性
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field field : declaredFields) {
                //判断当前Bean是否含有@MyAutowired注解
                if (field.getAnnotation(MyAutowired.class) != null) {
                    //@MyAutowired 只能标注在非静态属性上
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalArgumentException("@MyAutowired annotation is only can annotated on not static field");
                    }
                    Class<?> fieldClazz = field.getType();
                    //@MyAutowired 只能标注在接口类型属性上
                    if (!fieldClazz.isInterface()) {
                        throw new IllegalArgumentException("@MyAutowired annotation is only can annotated on interface class field");
                    }
                    //将@MyAutowired 标注的属性的FactoryBean注入Spring容器
                    this.registryProxyFactoryBean(fieldClazz, registry);
                }
            }
        }
    }

    /**
     * 将目标接口的FactoryBean注入到Spring容器中
     *
     * @param beanClass 接口beanClass
     * @param registry  注册器
     */
    private void registryProxyFactoryBean(Class<?> beanClass, BeanDefinitionRegistry registry) {
        //获取Bean名称
        String beanName = BeanUtils.getName(beanClass);
        //判断是否存在当前Bean定义信息
        if (!registry.containsBeanDefinition(beanName)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
            //将被目标类以工厂Bean的方式注入到Spring容器中
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            //设置beanClass属性为目标类的工厂Bean类对象
            beanDefinition.setBeanClass(MyServiceFactoryBean.class);
            //为工厂Bean的referenceInterface属性赋值
            beanDefinition.getPropertyValues().add("referenceInterface", beanClass);
            //设置注入方式，通过类型匹配
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            // 设置单例模式
            beanDefinition.setScope("singleton");

            //将封装好的BeanDefinition注入到Spring容器中
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

}
