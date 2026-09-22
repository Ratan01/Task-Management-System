package com.taskapp.task.feign;

import com.taskapp.common.dto.AuditEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-service")
public interface AuditClient {
    @PostMapping("/internal/audit/events")
    void record(@RequestBody AuditEvent event);
}