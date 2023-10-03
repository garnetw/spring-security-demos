package com.garnet.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }

    @GetMapping("/product")
    public String getProducts() {
        return "Product List";
    }

}
