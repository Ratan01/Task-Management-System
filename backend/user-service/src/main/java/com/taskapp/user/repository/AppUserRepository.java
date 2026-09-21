package com.taskapp.user.repository;

import com.taskapp.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByUsername(String username);
}