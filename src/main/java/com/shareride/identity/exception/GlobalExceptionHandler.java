package com.shareride.identity.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.shareride.identity.utils.Constants.CAUSE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleApplicationException(
            ApplicationException ex,
            HttpServletRequest request
    ) {
        var errorCode = ex.getErrorCode();
        var status = errorCode.getHttpStatus();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(ex.getDetails())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        var errorCode = ErrorCodes.UNAUTHORIZED;
        var status = errorCode.getHttpStatus();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(Map.of(CAUSE, ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        var errorCode = ErrorCodes.ACCESS_DENIED_INSUFFICIENT_ROLE;
        var status = errorCode.getHttpStatus();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(Map.of(CAUSE, ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        var errorCode = ErrorCodes.INVALID_INPUT;
        var status = errorCode.getHttpStatus();

        // Collect field-specific error messages
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // if duplicate keys, keep first
                ));

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(Map.of(CAUSE, fieldErrors))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        var errorCode = ErrorCodes.CONFLICT;
        var status = errorCode.getHttpStatus();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(Map.of(CAUSE, ex.getMostSpecificCause().getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }



    // fallback handler (for unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        var errorCode = ErrorCodes.GENERIC_ERROR;
        var status = errorCode.getHttpStatus();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(Map.of(CAUSE, ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
