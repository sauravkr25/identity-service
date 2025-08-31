package com.shareride.identity.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {

    // --- Client Errors (4xx) ---
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "ERR_400", "Invalid request"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "ERR_400_VALIDATION", "Validation failed"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ERR_401", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "ERR_403", "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_404", "Resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "ERR_409", "Conflict detected"),

    ACCESS_DENIED_INSUFFICIENT_ROLE(HttpStatus.FORBIDDEN, "ERR_403_ROLE", "Access denied: insufficient role/permissions"),
    JWT_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "ERR_401_JWT", "Invalid or expired JWT token"),

    // --- Server Errors (5xx) ---
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_500", "Internal server error"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "ERR_503", "Service unavailable"),
    GENERIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_500_GENERIC", "Generic Error");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCodes(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
