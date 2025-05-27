package com.nus.oribital.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nus.oribital.modal.Greeting;

@RestController
public class HelloController {

    @GetMapping("/sayHello")
    @CrossOrigin(origins = "http://localhost:8081")
	public Greeting greetUser(@RequestParam String name) {        
        return new Greeting("Hello " + name + "!", "Login is Successful");
	}

}