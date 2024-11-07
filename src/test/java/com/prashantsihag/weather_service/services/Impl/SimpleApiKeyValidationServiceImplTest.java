package com.prashantsihag.weather_service.services.Impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.prashantsihag.weather_service.services.interfaces.ApiKeyValidationService;

class SimpleApiKeyValidationServiceImplTest {

    private ApiKeyValidationService apiKeyValidationService;

    @BeforeEach
    void setUp() {
        apiKeyValidationService = new SimpleApiKeyValidationServiceImpl();
    }

    @Test
    void givenValidApiKey_whenIsValidApiKey_shouldReturnTrue() {
        assertTrue(apiKeyValidationService.isValidApiKey("API_KEY_1"));
        assertTrue(apiKeyValidationService.isValidApiKey("API_KEY_2"));
        assertTrue(apiKeyValidationService.isValidApiKey("API_KEY_3"));
        assertTrue(apiKeyValidationService.isValidApiKey("API_KEY_4"));
        assertTrue(apiKeyValidationService.isValidApiKey("API_KEY_5"));
    }

    @Test
    void givenInValidApiKeyValue_whenIsValidApiKey_shouldReturnFalse() {
        assertFalse(apiKeyValidationService.isValidApiKey("INVALID_API_KEY"));
        assertFalse(apiKeyValidationService.isValidApiKey("API_KEY_6"));
    }

    @Test
    void givenEmptyApiKeyValue_whenIsValidApiKey_shouldReturnFalse() {
        assertFalse(apiKeyValidationService.isValidApiKey(""));
    }

    @Test
    void givenNullApiKeyValue_whenIsValidApiKey_shouldReturnFalse() {
        assertFalse(apiKeyValidationService.isValidApiKey(null));
    }
}
