
package com.skillverse.reputation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReputationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReputationServiceApplication.class, args);
    }
}
