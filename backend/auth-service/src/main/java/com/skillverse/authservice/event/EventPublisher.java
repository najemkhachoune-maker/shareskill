package com.skillverse.authservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class EventPublisher {

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void publishUserEvent(EventType eventType, UUID userId, String email, Map<String, Object> payload) {
        UserEvent event = UserEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .userId(userId)
                .email(email)
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .serviceSource("auth-service")
                .build();

        String topic = getTopicForEventType(eventType);
        try {
            // Commented out to avoid blocking when Kafka is down
            // kafkaTemplate.send(topic, userId.toString(), event);
            log.info("Kafka is disabled - would have published event {} to topic {}", eventType, topic);
        } catch (Exception e) {
            log.error("Failed to publish event to Kafka (Kafka might be down): {}", e.getMessage());
            // We don't rethrow because we want the business logic to continue (e.g.
            // registration successful in DB)
        }
    }

    private String getTopicForEventType(EventType eventType) {
        return switch (eventType) {
            case USER_CREATED -> "user.created";
            case USER_UPDATED -> "user.updated";
            case USER_DELETED -> "user.deleted";
            case USER_LOGIN -> "user.login";
            case USER_LOGOUT -> "user.logout";
            case USER_ROLE_CHANGED -> "user.role.changed";
        };
    }
}
