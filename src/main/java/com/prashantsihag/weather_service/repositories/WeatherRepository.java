package com.prashantsihag.weather_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prashantsihag.weather_service.models.WeatherModel;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherModel, Long> {
    Optional<WeatherModel> findByQuery(String query);
}
