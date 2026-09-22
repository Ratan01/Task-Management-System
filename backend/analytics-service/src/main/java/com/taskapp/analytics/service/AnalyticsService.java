package com.taskapp.analytics.service;

import com.taskapp.analytics.dto.MetricsResponse;
import com.taskapp.analytics.feign.AuditClient;
import com.taskapp.analytics.feign.TaskClient;
import com.taskapp.analytics.feign.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final TaskClient taskClient;
    private final UserClient userClient;
    private final AuditClient auditClient;

    private volatile MetricsResponse cached;

    public AnalyticsService(TaskClient taskClient, UserClient userClient, AuditClient auditClient) {
        this.taskClient = taskClient;
        this.userClient = userClient;
        this.auditClient = auditClient;
    }

    public MetricsResponse current() {
        if (cached == null) {
            refresh();
        }
        return cached;
    }

    @Scheduled(fixedRate = 60_000)
    public void refresh() {
        try {
            List<Map<String, Object>> tasks = taskClient.allTasks(0L, "ADMIN");
            List<Map<String, Object>> users = userClient.allUsers("ADMIN");
            Instant since7d = Instant.now().minus(7, ChronoUnit.DAYS);
            Map<String, Long> auditCounts = auditClient.counts(since7d.toString());

            Map<String, Long> byStatus = new HashMap<>();
            long dueOrOverdue = 0;
            LocalDate today = LocalDate.now();
            for (Map<String, Object> t : tasks) {
                String status = String.valueOf(t.get("status"));
                byStatus.merge(status, 1L, Long::sum);
                Object due = t.get("dueDate");
                if (due != null && !"COMPLETED".equals(status)) {
                    LocalDate d = LocalDate.parse(String.valueOf(due));
                    if (!d.isAfter(today)) dueOrOverdue++;
                }
            }

            cached = new MetricsResponse(
                    Instant.now(),
                    users.size(),
                    tasks.size(),
                    byStatus,
                    auditCounts,
                    dueOrOverdue
            );
        } catch (Exception e) {
            log.warn("Analytics refresh failed: {}", e.getMessage());
        }
    }
}