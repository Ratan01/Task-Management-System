package com.taskapp.task.dto;

import com.taskapp.task.entity.TaskStatus;

import java.time.LocalDate;

public record TaskDto(Long id, String title, String description, Long ownerId,
                      LocalDate dueDate, TaskStatus status) {}