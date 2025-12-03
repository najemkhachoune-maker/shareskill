package com.example.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "firebase.enabled=false")
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
