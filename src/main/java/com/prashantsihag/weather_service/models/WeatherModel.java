package com.prashantsihag.weather_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class WeatherModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String query;
    private String description;

    public WeatherModel(String query, String description) {
        this.query = query;
        this.description = description;
    }

    public WeatherModel() {
    }
}
