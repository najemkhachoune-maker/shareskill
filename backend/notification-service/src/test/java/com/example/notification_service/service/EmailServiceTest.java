
package com.example.notification_service.service;

import com.example.notification_service.dto.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;   // faux expéditeur

    @InjectMocks
    private EmailService emailService;   // le service réel, avec le mock injecté

    @BeforeEach
    void init() {
        // defaultFrom est private: on l’injecte via ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "defaultFrom", "default@test.com");
    }

    @Test
    void sendEmail_withCustomFrom_shouldUseCustomFrom() {
        EmailRequest req = new EmailRequest();   // pas de ctor 4-args → on utilise les setters
        req.setFrom("user@test.com");
        req.setTo("receiver@test.com");
        req.setSubject("Hello");
        req.setBody("Message body");

        emailService.sendEmail(req);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertEquals("user@test.com", sent.getFrom());
        assertArrayEquals(new String[]{"receiver@test.com"}, sent.getTo());
        assertEquals("Hello", sent.getSubject());
        assertEquals("Message body", sent.getText());
    }

    @Test
    void sendEmail_withoutFrom_shouldFallbackToDefaultFrom() {
        EmailRequest req = new EmailRequest();
        req.setFrom(null); // ou "", le service gère les deux cas
        req.setTo("receiver@test.com");
        req.setSubject("Subject");
        req.setBody("Body");

        emailService.sendEmail(req);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertEquals("default@test.com", sent.getFrom()); // valeur injectée en @BeforeEach
    }

    @Test
    void sendEmail_whenMailSenderThrows_shouldWrapInRuntimeException() {
        EmailRequest req = new EmailRequest();
        req.setFrom("user@test.com");
        req.setTo("receiver@test.com");
        req.setSubject("Subject");
        req.setBody("Body");

        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> emailService.sendEmail(req));
        assertTrue(ex.getMessage().contains("Impossible d'envoyer l'email"));
    }
}
