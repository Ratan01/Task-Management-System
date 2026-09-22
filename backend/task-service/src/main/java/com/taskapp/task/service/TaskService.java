package com.taskapp.task.service;

import com.taskapp.common.dto.AuditEvent;
import com.taskapp.common.enums.Role;
import com.taskapp.task.dto.TaskDto;
import com.taskapp.task.dto.TaskRequest;
import com.taskapp.task.entity.Task;
import com.taskapp.task.entity.TaskStatus;
import com.taskapp.task.feign.AuditClient;
import com.taskapp.task.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final AuditClient auditClient;

    public TaskService(TaskRepository repository, AuditClient auditClient) {
        this.repository = repository;
        this.auditClient = auditClient;
    }

    public List<TaskDto> list(Long userId, Role role) {
        List<Task> tasks = role == Role.ADMIN
                ? repository.findAll()
                : repository.findByOwnerId(userId);

        return tasks.stream()
                .map(this::toDto)
                .toList();
    }

    public TaskDto create(Long userId, Role role, TaskRequest req) {
        Task t = new Task();

        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setDueDate(req.dueDate());
        t.setStatus(req.status() != null ? req.status() : TaskStatus.PENDING);

        if (role == Role.ADMIN && req.ownerId() != null) {
            t.setOwnerId(req.ownerId());
        } else {
            t.setOwnerId(userId);
        }

        Task saved = repository.save(t);

        emit(
                "TASK_CREATED",
                String.valueOf(saved.getId()),
                userId,
                null,
                "Task created"
        );

        return toDto(saved);
    }

    public TaskDto update(Long userId, Role role, Long id, TaskRequest req) {
        Task t = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found"
                        ));

        if (role != Role.ADMIN && !t.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not your task"
            );
        }

        if (req.title() != null) {
            t.setTitle(req.title());
        }

        if (req.description() != null) {
            t.setDescription(req.description());
        }

        if (req.dueDate() != null) {
            t.setDueDate(req.dueDate());
        }

        if (req.status() != null) {
            t.setStatus(req.status());
        }

        if (role == Role.ADMIN && req.ownerId() != null) {
            t.setOwnerId(req.ownerId());
        }

        Task updated = repository.save(t);

        emit(
                "TASK_UPDATED",
                String.valueOf(updated.getId()),
                userId,
                null,
                "Task updated"
        );

        return toDto(updated);
    }

    public void delete(Long userId, Role role, Long id) {
        Task t = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found"
                        ));

        if (role != Role.ADMIN && !t.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not your task"
            );
        }

        repository.delete(t);

        emit(
                "TASK_DELETED",
                String.valueOf(t.getId()),
                userId,
                null,
                "Task deleted"
        );
    }

    public List<TaskDto> dueOnOrBefore(LocalDate date) {
        return repository
                .findByDueDateLessThanEqualAndStatusNot(
                        date,
                        TaskStatus.COMPLETED
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    private TaskDto toDto(Task t) {
        return new TaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getOwnerId(),
                t.getDueDate(),
                t.getStatus()
        );
    }

    private void emit(
            String action,
            String resourceId,
            Long actorId,
            String actorUsername,
            String details
    ) {
        try {
            auditClient.record(
                    new AuditEvent(
                            actorId,
                            actorUsername,
                            action,
                            "TASK",
                            resourceId,
                            details,
                            Instant.now()
                    )
            );
        } catch (Exception ignored) {
            // Never fail the main path because of audit
        }
    }
}
