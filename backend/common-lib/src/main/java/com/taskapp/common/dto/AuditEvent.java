package com.taskapp.common.dto;

import java.time.Instant;

public record AuditEvent(
        Long actorId,
        String actorUsername,
        String action, // e.g. "TASK_CREATED", "USER_DELETED"
        String resourceType, // "TASK", "USER", "AUTH"
        String resourceId, // nullable
        String details, // free-form JSON or text
        Instant occurredAt) {
}