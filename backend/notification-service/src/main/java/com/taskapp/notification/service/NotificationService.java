package com.taskapp.notification.service;

import com.taskapp.notification.dto.TaskDto;
import com.taskapp.notification.dto.UserDto;
import com.taskapp.notification.feign.TaskClient;
import com.taskapp.notification.feign.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final TaskClient taskClient;
    private final UserClient userClient;
    private final EmailService emailService;

    private volatile List<TaskDto> latestDueTasks = new ArrayList<>();
    private final Set<String> sentReminders = ConcurrentHashMap.newKeySet();

    public NotificationService(TaskClient taskClient, UserClient userClient, EmailService emailService) {
        this.taskClient = taskClient;
        this.userClient = userClient;
        this.emailService = emailService;
    }

    public List<TaskDto> getLatestDueTasks() {
        return latestDueTasks;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refreshDueTasks() {
        try {
            List<TaskDto> tasks = taskClient.dueOnOrBefore(LocalDate.now().toString());
            this.latestDueTasks = tasks;
            log.info("Refreshed due tasks: {} found", tasks.size());
            for (TaskDto t : tasks) {
                String key = t.id() + ":" + LocalDate.now();
                if (!sentReminders.contains(key)) {
                    try {
                        UserDto owner = userClient.getUser(t.ownerId());
                        if (owner != null && owner.email() != null) {
                            emailService.sendDueReminder(owner.email(), t);
                        }
                        sentReminders.add(key);
                    } catch (Exception e) {
                        log.warn("Reminder failed for task {}: {}", t.id(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to refresh due tasks: {}", e.getMessage());
        }
    }
}