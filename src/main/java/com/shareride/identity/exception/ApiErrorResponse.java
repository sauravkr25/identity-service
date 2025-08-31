package com.shareride.identity.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private final String error;
    private String code;
    private String message;
    private final Map<String, Object> details;
    private final Map<String, Object> innerError;
    private final String path;
}
