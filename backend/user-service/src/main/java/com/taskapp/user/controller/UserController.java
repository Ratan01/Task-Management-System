package com.taskapp.user.controller;

import com.taskapp.common.enums.Role;
import com.taskapp.user.dto.CreateUserRequest;
import com.taskapp.user.dto.ProvisionRequest;
import com.taskapp.user.dto.UpdateUserRequest;
import com.taskapp.user.dto.UserDto;
import com.taskapp.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    private void requireAdmin(String role) {
        if (!Role.ADMIN.name().equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    @GetMapping("/me")
    public UserDto me(@RequestHeader("X-User-Id") Long userId) {
        return service.findById(userId);
    }

    @GetMapping
    public List<UserDto> list(
            @RequestHeader(value = "X-Role", required = false) String role) {
        requireAdmin(role);
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<UserDto> create(
            @RequestHeader(value = "X-Role", required = false) String role,
            @Valid @RequestBody CreateUserRequest req) {
        requireAdmin(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    public UserDto update(
            @RequestHeader(value = "X-Role", required = false) String role,
            @PathVariable Long id,
            @RequestBody UpdateUserRequest req) {
        requireAdmin(role);
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "X-Role", required = false) String role,
            @PathVariable Long id) {
        requireAdmin(role);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/internal/provision")
//    public UserDto provision(@RequestBody ProvisionRequest req) {
//        return service.provision(req);
//    }
}