package com.taskapp.audit.controller;

import com.taskapp.audit.entity.AuditLog;
import com.taskapp.audit.service.AuditService;
import com.taskapp.common.enums.Role;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    private void requireAdmin(String role) {
        if (!Role.ADMIN.name().equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    @GetMapping
    public List<AuditLog> recent(@RequestHeader("X-Role") String role,
                                 @RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since,
                                 @RequestParam(defaultValue = "100") int limit) {
        requireAdmin(role);
        if (since == null) since = Instant.now().minusSeconds(7 * 24 * 3600);
        return service.recent(since, Math.min(limit, 500));
    }

    @GetMapping("/actor/{actorId}")
    public List<AuditLog> byActor(@RequestHeader("X-Role") String role,
                                  @PathVariable Long actorId,
                                  @RequestParam(defaultValue = "100") int limit) {
        requireAdmin(role);
        return service.findByActor(actorId, Math.min(limit, 500));
    }

    @GetMapping("/action/{action}")
    public List<AuditLog> byAction(@RequestHeader("X-Role") String role,
                                   @PathVariable String action,
                                   @RequestParam(defaultValue = "100") int limit) {
        requireAdmin(role);
        return service.findByAction(action, Math.min(limit, 500));
    }
}