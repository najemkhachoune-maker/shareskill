package com.skillverse.authservice.event;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private UUID eventId;
    private EventType eventType;
    private UUID userId;
    private String email;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;
    private String serviceSource;
}
