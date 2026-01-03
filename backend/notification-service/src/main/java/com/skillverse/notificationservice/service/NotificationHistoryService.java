package com.skillverse.notificationservice.service;

import com.skillverse.notificationservice.entity.NotificationHistory;
import com.skillverse.notificationservice.repository.NotificationHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationHistoryService {

    private final NotificationHistoryRepository repository;

    public NotificationHistoryService(NotificationHistoryRepository repository) {
        this.repository = repository;
    }

    public List<NotificationHistory> findAll() {
        return repository.findAll();
    }

    public Optional<NotificationHistory> findById(Long id) {
        return repository.findById(id);
    }

    public NotificationHistory save(NotificationHistory history) {
        return repository.save(history);
    }
}
