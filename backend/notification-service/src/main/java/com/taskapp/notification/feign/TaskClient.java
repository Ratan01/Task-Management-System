package com.taskapp.notification.feign;

import com.taskapp.notification.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "task-service")
public interface TaskClient {
    @GetMapping("/internal/tasks/due")
    List<TaskDto> dueOnOrBefore(@RequestParam("date") String date);
}