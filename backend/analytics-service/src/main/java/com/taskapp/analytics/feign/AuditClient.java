package com.taskapp.analytics.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "audit-service")
public interface AuditClient {

    @GetMapping("/internal/audit/counts")
    Map<String, Long> counts(@RequestParam("since") String sinceIso);

    @GetMapping("/internal/audit/recent")
    List<Map<String, Object>> recent(@RequestParam("since") String sinceIso,
                                     @RequestParam("limit") int limit);
}