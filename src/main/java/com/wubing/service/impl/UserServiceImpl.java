package com.wubing.service.impl;

import com.wubing.custom.annotation.MyAutowired;
import com.wubing.service.HelloService;
import com.wubing.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author: WB
 * @version: v1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @MyAutowired
    private HelloService helloService;

    @Override
    public String sayHello(String username) {
        return helloService.sayHello(username);
    }
}
