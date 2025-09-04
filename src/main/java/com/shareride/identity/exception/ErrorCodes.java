package com.shareride.identity.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {

    // --- Client Errors (4xx) ---
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "ERR_400", "Invalid request"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "ERR_400_VALIDATION", "Validation failed"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "ERR_400_INVALID_INPUT", "One or more fields are invalid"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ERR_401", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "ERR_403", "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_404", "Resource not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_404_USER", "User not found"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR_404_ROLE", "Role not found"),
    CONFLICT(HttpStatus.CONFLICT, "ERR_409", "Duplicate or conflicting data"),

    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "ERR_429_RATE_LIMIT", "Rate limit exceeded. Please try again later."),

    INVALID_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "ERR_400_TOKEN", "Invalid or expired verification token"),
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
