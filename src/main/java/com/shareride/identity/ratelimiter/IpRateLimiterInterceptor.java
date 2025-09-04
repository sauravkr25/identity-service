package com.shareride.identity.ratelimiter;

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

@Component
public class IpRateLimiterInterceptor implements HandlerInterceptor {

    private  final RateLimiterService rateLimiterService;

    public IpRateLimiterInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddresss = request.getRemoteAddr();
        if (ipAddresss == null || ipAddresss.isBlank()) {
            throw ApplicationException.of(ErrorCodes.FORBIDDEN, Map.of(CAUSE, "Could not identify client IP address"));
        }

        Bucket tokenBucket = rateLimiterService.resolveBucketForIp(ipAddresss);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader(HEADER_RATE_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
            return true;
        }
        else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            throw ApplicationException.of(
                    ErrorCodes.RATE_LIMIT_EXCEEDED,
                    Map.of("retryAfterSeconds", waitForRefill)
            );
        }
    }

}
