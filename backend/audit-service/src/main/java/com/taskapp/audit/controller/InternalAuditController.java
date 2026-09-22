package com.taskapp.audit.controller;

import com.taskapp.audit.dto.AuditEventDto;
import com.taskapp.audit.entity.AuditLog;
import com.taskapp.audit.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/audit")
public class InternalAuditController {

    private final AuditService service;

    public InternalAuditController(AuditService service) {
        this.service = service;
    }

    @PostMapping("/events")
    public ResponseEntity<AuditLog> record(@Valid @RequestBody AuditEventDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.record(dto));
    }

    @GetMapping("/recent")
    public List<AuditLog> recent(@RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since,
                                 @RequestParam(defaultValue = "500") int limit) {
        if (since == null) since = Instant.now().minusSeconds(24 * 3600);
        return service.recent(since, Math.min(limit, 1000));
    }

    @GetMapping("/counts")
    public Map<String, Long> counts(@RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
        if (since == null) since = Instant.now().minusSeconds(7 * 24 * 3600);
        return service.countsSince(since);
    }
}