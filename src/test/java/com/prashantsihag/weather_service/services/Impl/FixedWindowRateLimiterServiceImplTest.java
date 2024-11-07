package com.prashantsihag.weather_service.services.Impl;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestPropertySource(properties = {
        "rateLimiter.timeWindowInMinutes=1",
        "rateLimiter.allowedTotalCallsInWindow=1"
})
public class FixedWindowRateLimiterServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedWindowRateLimiterServiceImplTest.class);

    private final int timeWindowInMinutes = 1;
    private final int allowedTotalCallsInWindow = 1;

    @Autowired
    private FixedWindowRateLimiterServiceImpl rateLimiterService;

    @Test
    public void whenIsRequestAllowed_givenRateUnderLimit_shouldReturnTrue() {
        String apiKey = "testApiKey";

        for (int i = 0; i < allowedTotalCallsInWindow; i++) {
            assertTrue(rateLimiterService.isRequestAllowed(apiKey), "Request should be allowed under the limit.");
        }
    }

    @Test
    public void whenIsRequestAllowed_givenRateOverLimit_shouldReturnFalse() {
        String apiKey = "testApiKey";

        for (int i = 0; i < allowedTotalCallsInWindow; i++) {
            rateLimiterService.isRequestAllowed(apiKey);
        }

        assertFalse(rateLimiterService.isRequestAllowed(apiKey),
                "Request should be denied after reaching the rate limit.");
    }

    @Test
    public void whenIsRequestAllowed_givenAppropriatePause_shouldReturnTrue() throws InterruptedException {
        String apiKey = "testApiKey";

        for (int i = 0; i < allowedTotalCallsInWindow; i++) {
            rateLimiterService.isRequestAllowed(apiKey);
        }

        LOGGER.info(
                "Putting thread to sleep for {} minutes to simulate and test rate limit usage resetting functionality",
                timeWindowInMinutes);
        TimeUnit.MINUTES.sleep(timeWindowInMinutes);

        assertTrue(rateLimiterService.isRequestAllowed(apiKey), "Request should be allowed after 1 minute reset.");
    }

    @Test
    public void whenIsRequestAllowed_givenConcurrentRequests_shouldReturnTrue() throws InterruptedException {
        String apiKey = "testApiKey";

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                assertTrue(rateLimiterService.isRequestAllowed(apiKey), "Request should be allowed.");
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }
}