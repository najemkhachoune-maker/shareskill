package com.example.notification_service.service;

import com.example.notification_service.entity.NotificationHistory;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationPushTest {

    @Mock
    private NotificationHistoryService historyService;

    @InjectMocks
    private NotificationPush notificationPush;

    private MockedStatic<FirebaseMessaging> firebaseStaticMock;
    private FirebaseMessaging firebaseMessagingMock;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock statique de FirebaseMessaging.getInstance()
        firebaseMessagingMock = mock(FirebaseMessaging.class);
        firebaseStaticMock = mockStatic(FirebaseMessaging.class);

        firebaseStaticMock.when(FirebaseMessaging::getInstance)
                .thenReturn(firebaseMessagingMock);
    }

    @AfterEach
    void teardown() {
        firebaseStaticMock.close();
    }

    @Test
    void testSendNotification_success() throws Exception {
        // Arrange
        String token = "abc123";
        String title = "Hello";
        String body = "World";

        // Mock succès FCM
        when(firebaseMessagingMock.send(any(Message.class)))
                .thenReturn("message_id_123");

        // Act
        notificationPush.sendNotification(token, title, body);

        // Assert : vérifier que l'API FCM a été appelée
        verify(firebaseMessagingMock, times(1)).send(any(Message.class));

        // Vérifier que l'historique est bien sauvegardé avec statut SENT
        ArgumentCaptor<NotificationHistory> captor =
                ArgumentCaptor.forClass(NotificationHistory.class);

        verify(historyService).save(captor.capture());
        NotificationHistory saved = captor.getValue();

        assertEquals("SENT", saved.getStatus());
        assertEquals(token, saved.getToken());
        assertEquals(title, saved.getTitle());
        assertEquals(body, saved.getBody());
        assertNotNull(saved.getDateSent());
    }

    @Test
    void testSendNotification_failure() throws Exception {
        // Arrange
        String token = "xyz999";
        String title = "Test";
        String body = "Erreur";

        // Mock FCM qui lance une exception
        when(firebaseMessagingMock.send(any(Message.class)))
                .thenThrow(new RuntimeException("FCM error"));

        // Act
        notificationPush.sendNotification(token, title, body);

        // Assert : vérifier l'appel FCM
        verify(firebaseMessagingMock, times(1)).send(any(Message.class));

        // Vérifier que l'historique est sauvegardé avec statut FAILED
        ArgumentCaptor<NotificationHistory> captor =
                ArgumentCaptor.forClass(NotificationHistory.class);

        verify(historyService).save(captor.capture());
        NotificationHistory saved = captor.getValue();

        assertEquals("FAILED", saved.getStatus());
        assertEquals(token, saved.getToken());
        assertEquals(title, saved.getTitle());
        assertEquals(body, saved.getBody());
        assertNotNull(saved.getDateSent());
    }
}
