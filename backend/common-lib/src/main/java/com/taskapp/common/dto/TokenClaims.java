package com.taskapp.common.dto;

import com.taskapp.common.enums.Role;

public record TokenClaims(Long userId, String username, Role role) {
}