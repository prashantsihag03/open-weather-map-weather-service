package com.prashantsihag.weather_service.services.Impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.prashantsihag.weather_service.services.interfaces.ApiKeyValidationService;

@Service
public class SimpleApiKeyValidationServiceImpl implements ApiKeyValidationService {
    private final Set<String> validApiKeys = Set.of("API_KEY_1", "API_KEY_2", "API_KEY_3", "API_KEY_4", "API_KEY_5");

    @Override
    public boolean isValidApiKey(String apiKey) {
        if (apiKey == null)
            return false;
        return validApiKeys.contains(apiKey);
    }
}
