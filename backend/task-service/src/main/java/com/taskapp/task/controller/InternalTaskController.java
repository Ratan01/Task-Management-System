package com.taskapp.task.controller;

import com.taskapp.task.dto.TaskDto;
import com.taskapp.task.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/internal/tasks")
public class InternalTaskController {

    private final TaskService service;

    public InternalTaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/due")
    public List<TaskDto> due(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.dueOnOrBefore(date);
    }
}