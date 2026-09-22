package com.taskapp.analytics.dto;

import java.time.Instant;
import java.util.Map;

public record MetricsResponse(
        Instant generatedAt,
        long totalUsers,
        long totalTasks,
        Map<String, Long> tasksByStatus,
        Map<String, Long> auditCountsLast7Days,
        long tasksDueTodayOrOverdue
) {}