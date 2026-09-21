package com.taskapp.user.controller;

import com.taskapp.user.dto.ProvisionRequest;
import com.taskapp.user.dto.UserDto;
import com.taskapp.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/internal")
public class InternalUserController {

    private final UserService service;

    public InternalUserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/provision")
    public ResponseEntity<UserDto> provision(@Valid @RequestBody ProvisionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.provision(req));
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return service.findById(id);
    }
}