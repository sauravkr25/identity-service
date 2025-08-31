package com.shareride.identity.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorCodes errorCode;
    private final Map<String, Object> details;
    private final Throwable cause;

    public ApplicationException(ErrorCodes errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.cause = cause;
        this.errorCode = errorCode;
        this.details = details;
    }

    public static ApplicationException of(ErrorCodes errorCode) {
        return ApplicationException.builder()
                .errorCode(errorCode)
                .build();
    }

    public static ApplicationException of(ErrorCodes errorCode, Map<String, Object> details) {
        return ApplicationException.builder()
                .errorCode(errorCode)
                .details(details)
                .build();
    }

    public static ApplicationException of(ErrorCodes errorCode, Map<String, Object> details, Throwable cause) {
        return ApplicationException.builder()
                .errorCode(errorCode)
                .details(details)
                .cause(cause)
                .build();
    }


}
