package com.wubing.controller;

import com.wubing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: WB
 * @version: v1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/hello")
    public String hello(HttpServletRequest request) {
        return userService.sayHello(request.getParameter("username"));
    }
}
