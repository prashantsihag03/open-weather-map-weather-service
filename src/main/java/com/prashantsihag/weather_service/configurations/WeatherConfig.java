package com.prashantsihag.weather_service.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "weather.api")
public class WeatherConfig {
    private String Key;
    private String baseUrl;
}