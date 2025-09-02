package com.shareride.identity.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static com.shareride.identity.utils.Constants.JWT_EXCEPTION;
import static com.shareride.identity.utils.Constants.CAUSE;

@Component
public class JwtAuthExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Throwable jwtEx = (Throwable) request.getAttribute(JWT_EXCEPTION);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var errorCode = ErrorCodes.JWT_AUTHENTICATION_FAILED;
        var status = errorCode.getHttpStatus();
        response.setStatus(status.value());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(Map.of(CAUSE, authException.getMessage(),JWT_EXCEPTION, jwtEx != null ? jwtEx.getMessage() : "null"))
                .path(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }


}
