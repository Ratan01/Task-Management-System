package com.taskapp.task.service;

import com.taskapp.common.enums.Role;
import com.taskapp.task.dto.TaskDto;
import com.taskapp.task.dto.TaskRequest;
import com.taskapp.task.entity.Task;
import com.taskapp.task.entity.TaskStatus;
import com.taskapp.task.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskDto> list(Long userId, Role role) {
        List<Task> tasks = role == Role.ADMIN
                ? repository.findAll()
                : repository.findByOwnerId(userId);
        return tasks.stream().map(this::toDto).toList();
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
        return toDto(repository.save(t));
    }

    public TaskDto update(Long userId, Role role, Long id, TaskRequest req) {
        Task t = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (role != Role.ADMIN && !t.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your task");
        }
        if (req.title() != null) t.setTitle(req.title());
        if (req.description() != null) t.setDescription(req.description());
        if (req.dueDate() != null) t.setDueDate(req.dueDate());
        if (req.status() != null) t.setStatus(req.status());
        if (role == Role.ADMIN && req.ownerId() != null) t.setOwnerId(req.ownerId());
        return toDto(repository.save(t));
    }

    public void delete(Long userId, Role role, Long id) {
        Task t = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (role != Role.ADMIN && !t.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your task");
        }
        repository.delete(t);
    }

    public List<TaskDto> dueOnOrBefore(LocalDate date) {
        return repository.findByDueDateLessThanEqualAndStatusNot(date, TaskStatus.COMPLETED)
                .stream().map(this::toDto).toList();
    }

    private TaskDto toDto(Task t) {
        return new TaskDto(t.getId(), t.getTitle(), t.getDescription(),
                t.getOwnerId(), t.getDueDate(), t.getStatus());
    }
}