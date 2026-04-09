package com.example.sms.common.api;

public record ApiResponse<T>(boolean success, String message, T data) {
    public static <T> ApiResponse<T> success(T data){ return new ApiResponse<>(true, "OK", data);}
    public static <T> ApiResponse<T> error(int code, String message){ return new ApiResponse<>(false, message, null);}
}
