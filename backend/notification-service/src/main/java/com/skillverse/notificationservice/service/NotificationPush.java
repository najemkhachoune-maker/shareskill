package com.skillverse.notificationservice.service;

import com.skillverse.notificationservice.dto.PushNotificationRequest;
import com.skillverse.notificationservice.entity.NotificationHistory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationPush {

    private final NotificationHistoryService historyService;

    public NotificationPush(NotificationHistoryService historyService) {
        this.historyService = historyService;
    }

    public void sendPush(PushNotificationRequest pushDTO) {
        sendNotification(pushDTO.getRecipient(), pushDTO.getTitle(), pushDTO.getMessage());
    }

    // Required by tests
    public void sendNotification(String recipient, String title, String message) {
        System.out.println("Sending push notification to " + recipient + ": " + title);

        NotificationHistory history = NotificationHistory.builder()
                .recipient(recipient)
                .title(title)
                .body(message)
                .message(message)
                .type("PUSH")
                .status("SENT")
                .dateSent(LocalDateTime.now())
                .sentAt(LocalDateTime.now())
                .build();
        historyService.save(history);
    }
}
