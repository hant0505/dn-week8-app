package com.example.securingweb;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlertWebhook {
    @PostMapping("/webhook")
    public String handleWebhook(@RequestBody String alert) {
        System.out.println("Alert received: " + alert);
        return "OK";
    }
}