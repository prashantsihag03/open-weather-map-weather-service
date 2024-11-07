package com.prashantsihag.weather_service.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prashantsihag.weather_service.DTOs.WeatherResponseDTO;
import com.prashantsihag.weather_service.exceptions.InvalidApiKeyException;
import com.prashantsihag.weather_service.exceptions.InvalidAuthorizationHeaderException;
import com.prashantsihag.weather_service.exceptions.RateLimitException;
import com.prashantsihag.weather_service.exceptions.WeatherDataUnavailableException;
import com.prashantsihag.weather_service.services.interfaces.ApiKeyValidationService;
import com.prashantsihag.weather_service.services.interfaces.RateLimiterService;
import com.prashantsihag.weather_service.services.interfaces.WeatherService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;
    private final ApiKeyValidationService apiKeyValidationService;
    private final RateLimiterService rateLimiterService;

    public WeatherController(WeatherService weatherService, ApiKeyValidationService apiKeyValidationService,
            RateLimiterService rateLimiterService) {
        this.weatherService = weatherService;
        this.apiKeyValidationService = apiKeyValidationService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("")
    public ResponseEntity<WeatherResponseDTO> get(@RequestParam String q,
            @RequestHeader(value = "Authorization", required = false) String authorizationValue)
            throws InvalidAuthorizationHeaderException, InvalidApiKeyException, RateLimitException,
            WeatherDataUnavailableException {

        if (authorizationValue == null) {
            LOGGER.error("Missing Authorization header. Request parameters: q={}", q);
            throw new InvalidAuthorizationHeaderException("Missing Api Key in Authorization Header!");
        }

        if (!authorizationValue.startsWith("Bearer ")) {
            LOGGER.error("Invalid Authorization header value. Request parameters: q={}", q);
            throw new InvalidAuthorizationHeaderException("Invalid Authorization header value!");
        }

        String apiKey = authorizationValue.substring(7);

        if (!apiKeyValidationService.isValidApiKey(apiKey)) {
            LOGGER.error("Invalid Api Key provided. Request parameters: q={}", q);
            throw new InvalidApiKeyException("Invalid Api Key provided!");
        }

        if (!rateLimiterService.isRequestAllowed(apiKey)) {
            LOGGER.error("Rate limit exceeded. Request parameters: q={}", q);
            throw new RateLimitException("Rate limit exceeded. Please wait before making additional requests.");
        }

        return ResponseEntity.ok(weatherService.getWeatherData(q));
    }
}
