package com.prashantsihag.weather_service.services.interfaces;

import com.prashantsihag.weather_service.DTOs.WeatherResponseDTO;
import com.prashantsihag.weather_service.exceptions.WeatherDataUnavailableException;

public interface WeatherService {
    WeatherResponseDTO getWeatherData(String query) throws WeatherDataUnavailableException;
}
