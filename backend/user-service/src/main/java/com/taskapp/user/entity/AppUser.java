package com.taskapp.user.entity;

import com.taskapp.common.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}