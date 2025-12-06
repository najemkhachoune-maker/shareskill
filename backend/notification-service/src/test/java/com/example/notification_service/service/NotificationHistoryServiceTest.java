package com.example.notification_service.service;

import com.example.notification_service.entity.NotificationHistory;
import com.example.notification_service.repository.NotificationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationHistoryServiceTest {

    @Mock
    private NotificationHistoryRepository repository;

    @InjectMocks
    private NotificationHistoryService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldCallRepositoryAndReturnSavedEntity() {
        NotificationHistory h = new NotificationHistory();
        h.setToken("tok123");
        h.setTitle("Hello");
        h.setBody("Body");
        h.setDateSent(LocalDateTime.now());
        h.setStatus("SENT");

        when(repository.save(h)).thenReturn(h);

        NotificationHistory result = service.save(h);

        assertSame(h, result);
        verify(repository, times(1)).save(h);
    }

    @Test
    void findAll_shouldReturnRepoList() {
        NotificationHistory h1 = new NotificationHistory();
        NotificationHistory h2 = new NotificationHistory();
        List<NotificationHistory> list = Arrays.asList(h1, h2);

        when(repository.findAll()).thenReturn(list);

        List<NotificationHistory> out = service.findAll();

        assertEquals(2, out.size());
        verify(repository).findAll();
    }

    @Test
    void findById_found_shouldReturnEntity() {
        NotificationHistory h = new NotificationHistory();
        h.setId(10L);

        when(repository.findById(10L)).thenReturn(Optional.of(h));

        NotificationHistory out = service.findById(10L);

        assertNotNull(out);
        assertEquals(10L, out.getId());
        verify(repository).findById(10L);
    }

    @Test
    void findById_notFound_shouldReturnNull() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        NotificationHistory out = service.findById(99L);

        assertNull(out);
        verify(repository).findById(99L);
    }
}
