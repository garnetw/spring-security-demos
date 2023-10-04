package com.garnet.security.controller;

import com.garnet.security.model.UserInfo;
import com.garnet.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserInfo login(@RequestBody UserInfo userInfo) {
        return userService.login(userInfo);
    }

    @PostMapping("/register")
    public UserInfo register() {
        //TODO
        return null;
    }


    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }

    @GetMapping("/product")
    public String getProducts() {
        return "Product Info List";
    }

}
