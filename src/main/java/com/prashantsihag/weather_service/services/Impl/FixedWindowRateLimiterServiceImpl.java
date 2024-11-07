package com.prashantsihag.weather_service.services.Impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.prashantsihag.weather_service.services.interfaces.RateLimiterService;

@Service
public class FixedWindowRateLimiterServiceImpl implements RateLimiterService {

    @Value("${rateLimiter.allowedTotalCallsInWindow}")
    private long rateLimit;

    @Value("${rateLimiter.timeWindowInMinutes}")
    private long timeWindowInMinutes;

    private final Map<String, ApiKeyUsage> usageRecords = new ConcurrentHashMap<>();

    @Override
    public boolean isRequestAllowed(String apiKey) {
        ApiKeyUsage usage = usageRecords.computeIfAbsent(apiKey, k -> new ApiKeyUsage());

        synchronized (usage) {
            long timeElapsed = ChronoUnit.MINUTES.between(usage.getFirstRequestTime(), LocalDateTime.now());

            if (timeElapsed >= timeWindowInMinutes) {
                usage.resetUsage();
            }
            if (usage.getRequestCount() < rateLimit) {
                usage.incrementRequestCount();
                return true;
            } else {
                return false;
            }
        }
    }

    private static class ApiKeyUsage {
        private LocalDateTime firstRequestTime;
        private int requestCount;

        public ApiKeyUsage() {
            this.firstRequestTime = LocalDateTime.now();
            this.requestCount = 0;
        }

        public LocalDateTime getFirstRequestTime() {
            return firstRequestTime;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public void resetUsage() {
            this.firstRequestTime = LocalDateTime.now();
            this.requestCount = 0;
        }

        public void incrementRequestCount() {
            this.requestCount++;
        }
    }
}