package com.skillverse.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is UP and running!");
    }
    
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("All systems operational");
    }
    
    @GetMapping("/info")
    public ResponseEntity<String> info() {
        String info = """
            Auth Service Information:
            - Status: Active
            - Port: 8080
            - Context Path: /api/auth
            - Database: PostgreSQL
            - Security: Keycloak + JWT
            """;
        return ResponseEntity.ok(info);
    }
}
