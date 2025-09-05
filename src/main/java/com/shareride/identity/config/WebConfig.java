package com.shareride.identity.config;

import com.shareride.identity.ratelimiter.EmailRateLimitingInterceptor;
import com.shareride.identity.ratelimiter.IpRateLimiterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.shareride.identity.utils.Constants.Routes.API_V1;
import static com.shareride.identity.utils.Constants.Routes.AUTH;
import static com.shareride.identity.utils.Constants.Routes.SEND_VERIFICATION_EMAIL;
import static com.shareride.identity.utils.Constants.Routes.VERIFY_EMAIL;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final IpRateLimiterInterceptor ipRateLimiterInterceptor;
    private final EmailRateLimitingInterceptor emailRateLimitingInterceptor;

    public WebConfig(IpRateLimiterInterceptor ipRateLimiterInterceptor, EmailRateLimitingInterceptor emailRateLimitingInterceptor) {
        this.ipRateLimiterInterceptor = ipRateLimiterInterceptor;
        this.emailRateLimitingInterceptor = emailRateLimitingInterceptor;
    }

    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(ipRateLimiterInterceptor)
                .addPathPatterns(API_V1 + AUTH + VERIFY_EMAIL);
        registry.addInterceptor(emailRateLimitingInterceptor)
                .addPathPatterns(API_V1 + AUTH + SEND_VERIFICATION_EMAIL);
    }
}
