package com.booking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Booking Service is running ✅";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

