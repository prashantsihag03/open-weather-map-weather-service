package com.prashantsihag.weather_service.services.Impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.prashantsihag.weather_service.DTOs.WeatherResponseDTO;
import com.prashantsihag.weather_service.configurations.WeatherConfig;
import com.prashantsihag.weather_service.exceptions.WeatherDataUnavailableException;
import com.prashantsihag.weather_service.models.WeatherModel;
import com.prashantsihag.weather_service.repositories.WeatherRepository;
import com.prashantsihag.weather_service.services.interfaces.WeatherService;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherServiceImpl.class);

    private final RestTemplate restTemplate;
    private final WeatherConfig weatherConfig;
    private final WeatherRepository weatherRepository;

    public WeatherServiceImpl(RestTemplateBuilder restTemplateBuilder, WeatherConfig weatherConfig,
            WeatherRepository weatherRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.weatherConfig = weatherConfig;
        this.weatherRepository = weatherRepository;
    }

    @Override
    public WeatherResponseDTO getWeatherData(String query) throws WeatherDataUnavailableException {
        Optional<WeatherModel> weatherModelOptional = weatherRepository.findByQuery(query);

        if (weatherModelOptional.isPresent()) {
            WeatherModel weatherEntity = weatherModelOptional.get();
            WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO();
            weatherResponseDTO.setSingleWeatherDescription(weatherEntity.getDescription());
            return weatherResponseDTO;
        }

        String url = UriComponentsBuilder.fromHttpUrl(weatherConfig.getBaseUrl() + "/weather")
                .queryParam("q", query)
                .queryParam("appid", weatherConfig.getKey())
                .queryParam("units", "metric")
                .toUriString();

        WeatherResponseDTO weatherResponseDTO = restTemplate.getForObject(url, WeatherResponseDTO.class);

        if (weatherResponseDTO == null || weatherResponseDTO.getWeather() == null) {
            WeatherDataUnavailableException weatherDataUnavailableException = new WeatherDataUnavailableException(
                    "Weather data not available. Please try again later!");
            LOGGER.error("OpenWeatherMap response parsing resulted in null object {}", weatherDataUnavailableException);
            throw weatherDataUnavailableException;
        }

        String description = weatherResponseDTO.getWeather();
        WeatherModel weatherEntity = new WeatherModel(query, description);
        weatherRepository.save(weatherEntity);
        return weatherResponseDTO;
    }

}
