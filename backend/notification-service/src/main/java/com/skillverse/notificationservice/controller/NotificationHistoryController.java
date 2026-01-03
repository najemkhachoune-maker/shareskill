package com.skillverse.notificationservice.controller;

import com.skillverse.notificationservice.entity.NotificationHistory;
import com.skillverse.notificationservice.service.NotificationHistoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class NotificationHistoryController {

    private final NotificationHistoryService service;

    public NotificationHistoryController(NotificationHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationHistory> getAll() {
        return service.findAll();
    }

    @PostMapping
    public NotificationHistory create(@RequestBody NotificationHistory history) {
        return service.save(history);
    }
}
