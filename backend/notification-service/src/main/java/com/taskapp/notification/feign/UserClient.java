package com.taskapp.notification.feign;

import com.taskapp.notification.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/internal/{id}")
    UserDto getUser(@PathVariable("id") Long id);
}