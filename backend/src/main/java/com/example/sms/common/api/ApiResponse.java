package com.example.sms.common.api;

public record ApiResponse<T>(boolean success, String message, T data) {
    public static <T> ApiResponse<T> success(T data){ return new ApiResponse<>(true, "OK", data);}
}
