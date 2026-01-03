package com.skillverse.notificationservice.service;

import com.skillverse.notificationservice.dto.EmailRequest;
import com.skillverse.notificationservice.entity.NotificationHistory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private final NotificationHistoryService historyService;

    public EmailService(NotificationHistoryService historyService) {
        this.historyService = historyService;
    }

    public void sendEmail(EmailRequest emailDTO) {
        // Logic to send email (mocked for now)
        System.out.println("Sending email to " + emailDTO.getTo());

        NotificationHistory history = NotificationHistory.builder()
                .recipient(emailDTO.getTo())
                .subject(emailDTO.getSubject())
                .message(emailDTO.getBody())
                .body(emailDTO.getBody())
                .type("EMAIL")
                .status("SENT")
                .dateSent(LocalDateTime.now())
                .sentAt(LocalDateTime.now())
                .build();
        historyService.save(history);
    }
}
