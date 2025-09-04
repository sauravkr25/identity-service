package com.shareride.identity.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String baseUrl;
    private String supportEmail;
    private VerificationToken verificationToken = new VerificationToken();
    private Email email = new Email();
    private RateLimiter rateLimiter = new RateLimiter();

    @Data
    public static class VerificationToken {
        private int expiryInMillis;
        private int bytes;
    }

    @Data
    public static class Email {
        private Set<String> publicDomains = new HashSet<>();
    }

    @Data
    public static class RateLimiter {
        private LimiterConfig  email = new LimiterConfig ();
        private LimiterConfig  ip = new LimiterConfig ();
    }

    @Data
    public static class LimiterConfig {
        private int capacity;
        private int refillTokens;
        private int refillSeconds;
    }

}
