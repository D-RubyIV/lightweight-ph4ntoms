package com.ph4ntoms.authenticate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "test")
public class TestSecurity {
    @GetMapping(value = "test")
    public String test() {
       return "Hello";
    }
}
