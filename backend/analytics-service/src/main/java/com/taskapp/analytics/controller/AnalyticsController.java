package com.taskapp.analytics.controller;

import com.taskapp.analytics.dto.MetricsResponse;
import com.taskapp.analytics.service.AnalyticsService;
import com.taskapp.common.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/metrics")
    public MetricsResponse metrics(@RequestHeader("X-Role") String role) {
        if (!Role.ADMIN.name().equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
        return service.current();
    }

    @PostMapping("/refresh")
    public MetricsResponse refresh(@RequestHeader("X-Role") String role) {
        if (!Role.ADMIN.name().equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
        service.refresh();
        return service.current();
    }
}