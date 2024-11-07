package com.prashantsihag.weather_service.services.interfaces;

public interface RateLimiterService {
    boolean isRequestAllowed(String apiKey);
}
