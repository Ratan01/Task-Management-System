package com.taskapp.analytics.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "task-service")
public interface TaskClient {

    @GetMapping("/tasks")
    List<Map<String, Object>> allTasks(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role);
}