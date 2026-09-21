package com.taskapp.task.dto;

import com.taskapp.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank String title,
        String description,
        Long ownerId,
        LocalDate dueDate,
        TaskStatus status
) {}