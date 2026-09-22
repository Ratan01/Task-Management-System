package com.taskapp.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record AuditEventDto(
        Long actorId,
        String actorUsername,
        @NotBlank String action,
        @NotBlank String resourceType,
        String resourceId,
        String details,
        @NotNull Instant occurredAt
) {}