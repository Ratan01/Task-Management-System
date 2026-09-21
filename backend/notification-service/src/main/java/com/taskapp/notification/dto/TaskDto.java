package com.taskapp.notification.dto;

import java.time.LocalDate;

public record TaskDto(Long id, String title, String description, Long ownerId,
                      LocalDate dueDate, String status) {}