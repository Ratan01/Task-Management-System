package com.taskapp.notification.service;

import com.taskapp.notification.dto.TaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.notifications.email.enabled:false}") boolean enabled,
                        @Value("${app.notifications.email.from:no-reply@taskapp.local}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    public void sendDueReminder(String to, TaskDto task) {
        if (!enabled) {
            log.info("[email-disabled] Would email {} about task '{}' due {}", to, task.title(), task.dueDate());
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject("Task reminder: " + task.title());
            msg.setText("Task: " + task.title() + "\nDue: " + task.dueDate() + "\nStatus: " + task.status());
            mailSender.send(msg);
            log.info("Sent reminder email to {} for task {}", to, task.id());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }
}