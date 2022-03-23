package com.wubing.test;

import com.wubing.CustomAnnotationApplication;
import com.wubing.service.HelloService;
import com.wubing.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: WB
 * @version: v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomAnnotationApplication.class)
public class CustomSpringAnnotationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private HelloService helloService;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 自定义注解实现将Bean注入到Spring容器
     * 测试自定义注解使用在类上
     */
    @Test
    public void testAnnotatedClass() {
        String result = userService.sayHello("张三-Class");
        System.out.println(result);
    }

    /**
     * 自定义注解实现将Bean注入到Spring容器
     * 测试自定义注解使用在类上
     */
    @Test
    public void testAnnotatedInterface() {
        String result = helloService.sayHello("李四-Interface");
        System.out.println(result);
    }

    @Test
    public void testMyAnnotationRegister() {
        HelloService helloService = applicationContext.getBean("helloService", HelloService.class);
        System.out.println(helloService.sayHello("李四"));
    }


}
