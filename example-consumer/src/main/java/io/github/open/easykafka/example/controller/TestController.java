package io.github.open.easykafka.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author studeyang
 */
@RestController
public class TestController {

    @RequestMapping("/test")
    public String send() {
        return "success";
    }

}