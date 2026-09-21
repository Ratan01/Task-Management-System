package com.taskapp.task.controller;

import com.taskapp.common.enums.Role;
import com.taskapp.task.dto.TaskDto;
import com.taskapp.task.dto.TaskRequest;
import com.taskapp.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskDto> list(@RequestHeader("X-User-Id") Long userId,
                              @RequestHeader("X-Role") Role role) {
        return service.list(userId, role);
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestHeader("X-User-Id") Long userId,
                                          @RequestHeader("X-Role") Role role,
                                          @Valid @RequestBody TaskRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, role, req));
    }

    @PutMapping("/{id}")
    public TaskDto update(@RequestHeader("X-User-Id") Long userId,
                          @RequestHeader("X-Role") Role role,
                          @PathVariable Long id,
                          @RequestBody TaskRequest req) {
        return service.update(userId, role, id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                       @RequestHeader("X-Role") Role role,
                                       @PathVariable Long id) {
        service.delete(userId, role, id);
        return ResponseEntity.noContent().build();
    }
}