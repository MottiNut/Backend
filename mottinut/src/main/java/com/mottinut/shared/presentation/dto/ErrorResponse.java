package com.mottinut.shared.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class ErrorResponse {
    // Getters and Setters
    private String message;
    private List<String> errors;
    private int status;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, int status, String path) {
        this();
        this.message = message;
        this.status = status;
        this.path = path;
    }

    public ErrorResponse(String message, List<String> errors, int status, String path) {
        this();
        this.message = message;
        this.errors = errors;
        this.status = status;
        this.path = path;
    }

}

