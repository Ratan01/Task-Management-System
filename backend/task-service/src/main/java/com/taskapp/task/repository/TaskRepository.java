package com.taskapp.task.repository;

import com.taskapp.task.entity.Task;
import com.taskapp.task.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerId(Long ownerId);
    List<Task> findByOwnerIdAndStatusNot(Long ownerId, TaskStatus status);
    List<Task> findByDueDateLessThanEqualAndStatusNot(LocalDate dueDate, TaskStatus status);
}