package com.example.gatewayservice;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class GatewayController {

    private final Environment env;

    @GetMapping("/health-check")
    public String status(){

        return String.format("It's Working port : %s, token secret: %s, token expiration_time: %s "
                ,env.getProperty("local.server.port")
                ,env.getProperty("token.secret")
                ,env.getProperty("token.expiration_time"));
    }


}
