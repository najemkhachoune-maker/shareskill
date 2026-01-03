package com.skillverse.notificationservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushNotificationRequest {
    private String recipient;
    private String title;
    private String message;
    private String body;
    private String token;
}
