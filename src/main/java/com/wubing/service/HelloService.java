package com.wubing.service;

import com.wubing.custom.annotation.MyService;

/**
 * @author: WB
 * @version: v1.0
 */
@MyService
public interface HelloService {

    public String sayHello(String username);

}
