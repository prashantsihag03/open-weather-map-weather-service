package com.prashantsihag.weather_service.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class WeatherResponseDTO {

    @Setter
    @JsonProperty("weather")
    private List<WeatherDTO> weather;

    public String getWeather() {
        if (weather == null)
            return null;
        if (weather.isEmpty())
            return "";
        return weather.get(0).getDescription();
    }

    public void setSingleWeatherDescription(String description) {
        WeatherDTO weatherDTO = new WeatherDTO();
        weatherDTO.setDescription(description);

        if (weather == null) {
            weather = new ArrayList<>();
        }
        weather.add(weatherDTO);
    }

    public static class WeatherDTO {
        @Getter
        @Setter
        private String description;
    }
}