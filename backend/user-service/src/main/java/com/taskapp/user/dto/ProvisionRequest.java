package com.taskapp.user.dto;

import com.taskapp.common.enums.Role;

public record ProvisionRequest(Long id, String username, Role role, String email) {}