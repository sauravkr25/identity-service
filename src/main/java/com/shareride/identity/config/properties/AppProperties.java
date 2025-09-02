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

    @Data
    public static class VerificationToken {
        private int expiryInMillis;
        private int bytes;
    }

    @Data
    public static class Email {
        private Set<String> publicDomains = new HashSet<>();
    }

}
