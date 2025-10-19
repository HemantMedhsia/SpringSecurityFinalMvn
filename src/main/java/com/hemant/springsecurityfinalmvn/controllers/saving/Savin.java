package com.hemant.springsecurityfinalmvn.controllers.saving;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class Savin {
    @GetMapping("hello")
    public String sayHello() {
        return "Hello Hemant! 👋 Your Spring Boot server is up and running!";
    }
}
