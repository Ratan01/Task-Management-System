package com.taskapp.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/users/internal/provision")
    Map<String, Object> provision(
            @RequestHeader("X-Internal-Token") String internalToken,
            @RequestBody Map<String, Object> body);
}