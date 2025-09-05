package com.shareride.identity.ratelimiter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shareride.identity.api.request.EmailRequest;
import com.shareride.identity.exception.ApplicationException;
import com.shareride.identity.exception.ErrorCodes;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.shareride.identity.utils.Constants.CAUSE;
import static com.shareride.identity.utils.Constants.HEADER_RATE_LIMIT_REMAINING;
import static com.shareride.identity.utils.Constants.RETRY_AFTER_SECONDS;

@Component
public class EmailRateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    public EmailRateLimitingInterceptor(RateLimiterService rateLimiterService, ObjectMapper objectMapper) {
        this.rateLimiterService = rateLimiterService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        EmailRequest emailRequest = objectMapper.readValue(request.getInputStream(), EmailRequest.class);
        String email = emailRequest.getEmail();

        if (email == null || email.isBlank()) {
            throw ApplicationException.of(ErrorCodes.BAD_REQUEST, Map.of(CAUSE, "Could not read email from request"));
        }

        Bucket tokenBucket = rateLimiterService.resolveBucketForEmail(email);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader(HEADER_RATE_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
            return true;
        }
        else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            throw ApplicationException.of(
                    ErrorCodes.RATE_LIMIT_EXCEEDED,
                    Map.of(CAUSE,Map.of(RETRY_AFTER_SECONDS, waitForRefill))
            );
        }
    }
}
