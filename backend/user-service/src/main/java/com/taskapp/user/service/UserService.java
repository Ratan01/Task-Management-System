package com.taskapp.user.service;

import com.taskapp.common.enums.Role;
import com.taskapp.user.dto.CreateUserRequest;
import com.taskapp.user.dto.ProvisionRequest;
import com.taskapp.user.dto.UpdateUserRequest;
import com.taskapp.user.dto.UserDto;
import com.taskapp.user.entity.AppUser;
import com.taskapp.user.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final AppUserRepository repository;

    public UserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public List<UserDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto findById(Long id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserDto create(CreateUserRequest req) {
        if (repository.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        AppUser u = new AppUser();
        u.setId(System.currentTimeMillis()); // placeholder; provisioning normally sets id
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setRole(req.role());
        return toDto(repository.save(u));
    }

    public UserDto provision(ProvisionRequest req) {
        AppUser u = repository.findById(req.id()).orElseGet(AppUser::new);
        u.setId(req.id());
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setRole(req.role() != null ? req.role() : Role.USER);
        return toDto(repository.save(u));
    }

    public UserDto update(Long id, UpdateUserRequest req) {
        AppUser u = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (req.email() != null) u.setEmail(req.email());
        if (req.role() != null) u.setRole(req.role());
        return toDto(repository.save(u));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        repository.deleteById(id);
    }

    private UserDto toDto(AppUser u) {
        return new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
    }
}