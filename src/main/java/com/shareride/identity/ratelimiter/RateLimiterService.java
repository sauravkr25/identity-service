package com.shareride.identity.ratelimiter;

import com.shareride.identity.config.properties.AppProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.shareride.identity.utils.Constants.EMAIL_PREFIX;
import static com.shareride.identity.utils.Constants.IP_PREFIX;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    private final AppProperties appProperties;

    public RateLimiterService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public Bucket resolveBucketForEmail(String email) {
        String key = EMAIL_PREFIX + email;
        return bucketCache.computeIfAbsent(key, k -> newEmailBucket());
    }

    private Bucket newEmailBucket() {
        AppProperties.LimiterConfig config = appProperties.getRateLimiter().getEmail();
        Bandwidth limit = Bandwidth.classic(
                config.getCapacity(),
                Refill.intervally(config.getRefillTokens(), Duration.ofSeconds(config.getRefillSeconds()))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public Bucket resolveBucketForIp(String ip) {
        String key = IP_PREFIX + ip;
        return bucketCache.computeIfAbsent(key, k -> newIpBucket());
    }

    private Bucket newIpBucket() {
        AppProperties.LimiterConfig config = appProperties.getRateLimiter().getIp();
        Bandwidth limit = Bandwidth.classic(
                config.getCapacity(),
                Refill.intervally(config.getRefillTokens(), Duration.ofSeconds(config.getRefillSeconds()))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
