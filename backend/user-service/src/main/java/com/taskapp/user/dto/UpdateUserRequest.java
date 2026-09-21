package com.taskapp.user.dto;

import com.taskapp.common.enums.Role;

public record UpdateUserRequest(String email, Role role) {}