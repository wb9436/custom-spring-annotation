package com.wubing;

import com.wubing.custom.annotation.EnableMyAnnotation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: WB
 * @version: v1.0
 */
@SpringBootApplication
@EnableMyAnnotation //开启自定义注解支持
public class CustomAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomAnnotationApplication.class);
    }
}
