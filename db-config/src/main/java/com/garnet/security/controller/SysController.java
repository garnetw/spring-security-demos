package com.garnet.security.controller;

import com.garnet.security.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysController {

    @Autowired
    DaoAuthenticationProvider authenticationProvider;

    @PostMapping("/login")
    public String login(@RequestBody UserInfo user) {

        Authentication token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authentication = authenticationProvider.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "登陆成功";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }

    @GetMapping("/product")
    public String getList() {
        return "Product Info List";
    }

}
