package com.taskapp.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatus(ResponseStatusException ex) {
        return build(ex.getStatusCode().value(), ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Validation error");
        return build(HttpStatus.BAD_REQUEST.value(), msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal error");
    }

    private ResponseEntity<Map<String, Object>> build(int status, String msg) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("message", msg);
        return ResponseEntity.status(status).body(body);
    }
}