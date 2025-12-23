package com.marvin.campustrade.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TestWsController {

    @MessageMapping("/test")
    @SendTo("/topic/test")
    public String test(String payload, Principal principal) {
        System.out.println("WS MESSAGE FROM: " + principal.getName());
        return "Echo: " + payload;
    }
}
