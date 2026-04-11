package com.example.sms.common.exception;

import com.example.sms.common.api.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", message);
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntime(RuntimeException e) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        return ApiResponse.error(500, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGeneral(Exception e) {
        log.error("Unexpected exception: {}", e.getMessage(), e);
        return ApiResponse.error(500, "服务器内部错误");
    }
}
