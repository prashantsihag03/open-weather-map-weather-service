package com.prashantsihag.weather_service.services.Impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import com.prashantsihag.weather_service.DTOs.WeatherResponseDTO;
import com.prashantsihag.weather_service.configurations.WeatherConfig;
import com.prashantsihag.weather_service.exceptions.WeatherDataUnavailableException;
import com.prashantsihag.weather_service.models.WeatherModel;
import com.prashantsihag.weather_service.repositories.WeatherRepository;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherConfig weatherConfig;

    @Mock
    private WeatherRepository weatherRepository;

    private WeatherServiceImpl weatherServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
        weatherServiceImpl = new WeatherServiceImpl(restTemplateBuilder, weatherConfig, weatherRepository);
    }

    @Test
    void givenQueryStoredInDBAlready_whenGetWeatherData_shouldReturnFromDatabase()
            throws WeatherDataUnavailableException {
        String query = "London";
        String description = "Clear sky";
        WeatherModel weatherModel = new WeatherModel(query, description);
        Mockito.when(weatherRepository.findByQuery(query)).thenReturn(Optional.of(weatherModel));

        WeatherResponseDTO result = weatherServiceImpl.getWeatherData(query);

        assertNotNull(result);
        assertEquals(description, result.getWeather());
        Mockito.verify(weatherRepository, Mockito.times(1)).findByQuery(query);
        Mockito.verify(restTemplate, Mockito.never()).getForObject(Mockito.anyString(),
                Mockito.eq(WeatherResponseDTO.class));
    }

    @Test
    void givenQueryNotPresentInDB_whenGetWeatherData_shouldReturnFromApiAndSaveToDatabase()
            throws WeatherDataUnavailableException {
        String query = "New York";
        String description = "Partly cloudy";
        WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO();
        weatherResponseDTO.setSingleWeatherDescription(description);

        Mockito.when(weatherConfig.getBaseUrl()).thenReturn("http://api.openweathermap.org/data/2.5");
        Mockito.when(weatherConfig.getKey()).thenReturn("test_api_key");
        Mockito.when(weatherRepository.findByQuery(query)).thenReturn(Optional.empty());
        Mockito.when(restTemplate.getForObject(Mockito
                .anyString(), Mockito.eq(WeatherResponseDTO.class)))
                .thenReturn(weatherResponseDTO);

        WeatherResponseDTO result = weatherServiceImpl.getWeatherData(query);

        assertNotNull(result);
        assertEquals(description, result.getWeather());
        Mockito.verify(weatherRepository, Mockito.times(1)).findByQuery(query);
        Mockito.verify(restTemplate, Mockito.times(1)).getForObject(Mockito.anyString(),
                Mockito.eq(WeatherResponseDTO.class));
        Mockito.verify(weatherRepository, Mockito.times(1)).save(Mockito.any(WeatherModel.class));
    }

    @Test
    void givenQueryNotPresentInDB_whenGetWeatherDataApiReturnsNull_shouldThrowWeatherDataUnavailableException()
            throws WeatherDataUnavailableException {
        String query = "Los Angeles";

        Mockito.when(weatherConfig.getBaseUrl()).thenReturn("http://api.openweathermap.org/data/2.5");
        Mockito.when(weatherConfig.getKey()).thenReturn("test_api_key");
        Mockito.when(weatherRepository.findByQuery(query)).thenReturn(Optional.empty());
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(WeatherResponseDTO.class)))
                .thenReturn(null);

        WeatherDataUnavailableException exception = assertThrows(WeatherDataUnavailableException.class, () -> {
            weatherServiceImpl.getWeatherData(query);
        });

        assertEquals("Weather data not available. Please try again later!", exception.getMessage());
        Mockito.verify(weatherRepository, Mockito.times(1)).findByQuery(query);
        Mockito.verify(restTemplate, Mockito.times(1)).getForObject(Mockito.anyString(),
                Mockito.eq(WeatherResponseDTO.class));
        Mockito.verify(weatherRepository, Mockito.never()).save(Mockito.any(WeatherModel.class));
    }

    @Test
    void givenQueryNotPresentInDB_whenGetWeatherDataApiDoesNotRespondWithWeatherDesc_shouldThrowWeatherDataUnavailableException()
            throws WeatherDataUnavailableException {
        String query = "Los Angeles";

        when(weatherConfig.getBaseUrl()).thenReturn("http://api.openweathermap.org/data/2.5");
        when(weatherConfig.getKey()).thenReturn("test_api_key");
        when(weatherRepository.findByQuery(query)).thenReturn(Optional.empty());

        WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO();
        when(restTemplate.getForObject(anyString(), Mockito.eq(WeatherResponseDTO.class)))
                .thenReturn(weatherResponseDTO);

        WeatherDataUnavailableException exception = assertThrows(WeatherDataUnavailableException.class, () -> {
            weatherServiceImpl.getWeatherData(query);
        });

        assertEquals("Weather data not available. Please try again later!", exception.getMessage());
        verify(weatherRepository, Mockito.times(1)).findByQuery(query);
        verify(restTemplate, Mockito.times(1)).getForObject(anyString(), Mockito.eq(WeatherResponseDTO.class));
        verify(weatherRepository, Mockito.never()).save(Mockito.any(WeatherModel.class));
    }
}
