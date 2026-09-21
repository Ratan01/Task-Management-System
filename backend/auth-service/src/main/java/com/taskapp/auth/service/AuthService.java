package com.taskapp.auth.service;

import com.taskapp.auth.dto.AuthResponse;
import com.taskapp.auth.dto.LoginRequest;
import com.taskapp.auth.dto.RegisterRequest;
import com.taskapp.auth.entity.Account;
import com.taskapp.auth.feign.UserClient;
import com.taskapp.auth.repository.AccountRepository;
import com.taskapp.common.enums.Role;
import com.taskapp.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AccountRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;

    public AuthService(AccountRepository repository,
                       PasswordEncoder encoder,
                       UserClient userClient,
                       @Value("${app.jwt.secret}") String secret,
                       @Value("${app.jwt.expiration-seconds:86400}") long expiration) {
        this.repository = repository;
        this.encoder = encoder;
        this.userClient = userClient;
        this.jwtUtil = new JwtUtil(secret, expiration);
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (repository.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        Account account = new Account();
        account.setUsername(req.username());
        account.setPasswordHash(encoder.encode(req.password()));
        account.setRole(Role.USER);
        account = repository.save(account);

        Map<String, Object> body = new HashMap<>();
        body.put("id", account.getId());
        body.put("username", account.getUsername());
        body.put("role", account.getRole().name());
        try {
            userClient.provision("", body);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to provision user-service account: " + e.getMessage());
        }

        String token = jwtUtil.generate(account.getId(), account.getUsername(), account.getRole());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        Account account = repository.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(req.password(), account.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtUtil.generate(account.getId(), account.getUsername(), account.getRole());
        return new AuthResponse(token);
    }
}