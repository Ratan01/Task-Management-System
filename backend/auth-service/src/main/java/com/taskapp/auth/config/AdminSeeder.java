package com.taskapp.auth.config;

import com.taskapp.auth.entity.Account;
import com.taskapp.auth.repository.AccountRepository;

import com.taskapp.common.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AccountRepository repository;
    private final PasswordEncoder encoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminSeeder(AccountRepository repository,
                       PasswordEncoder encoder,
                       @Value("${app.admin.username:admin}") String adminUsername,
                       @Value("${app.admin.password:}") String adminPassword) {
        this.repository = repository;
        this.encoder = encoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("No accounts exist and ADMIN_PASSWORD not set. Skipping admin seed.");
            return;
        }
        Account admin = new Account();
        admin.setUsername(adminUsername);
        admin.setPasswordHash(encoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        repository.save(admin);
        log.info("Seeded initial admin user '{}'", adminUsername);
    }
}