package com.taskapp.notification.controller;

import com.taskapp.notification.dto.TaskDto;
import com.taskapp.notification.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/due-today")
    public List<TaskDto> dueToday() {
        return service.getLatestDueTasks();
    }
}