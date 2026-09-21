package com.taskapp.user.dto;

import com.taskapp.common.enums.Role;

public record UserDto(Long id, String username, String email, Role role) {}