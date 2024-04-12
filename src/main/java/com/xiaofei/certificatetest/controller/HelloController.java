package com.xiaofei.certificatetest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: xiaofei
 * Date: 2024-04-12, 0:09
 * Description:
 */
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String getHello(){
        return "hello world";
    }
}
