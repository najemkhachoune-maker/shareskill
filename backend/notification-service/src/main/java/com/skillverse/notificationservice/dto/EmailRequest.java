package com.skillverse.notificationservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {
    private String to;
    private String from;
    private String recipient;
    private String subject;
    private String body;
}
