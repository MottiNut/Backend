package com.mottinut.shared.presentation.dto;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean success;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(String message, T data) {
        this();
        this.message = message;
        this.data = data;
        this.success = true;
    }

    public ApiResponse(String message, T data, boolean success) {
        this();
        this.message = message;
        this.data = data;
        this.success = success;
    }

    // ✅ Métodos de fábrica
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, true);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null, true);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(message, data, false);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, false);
    }
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("Success", data, true);
    }


    // ✅ Getters y Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
