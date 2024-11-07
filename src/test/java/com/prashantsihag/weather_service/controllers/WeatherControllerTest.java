package com.prashantsihag.weather_service.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import com.prashantsihag.weather_service.DTOs.WeatherResponseDTO;
import com.prashantsihag.weather_service.exceptions.WeatherDataUnavailableException;
import com.prashantsihag.weather_service.services.interfaces.ApiKeyValidationService;
import com.prashantsihag.weather_service.services.interfaces.RateLimiterService;
import com.prashantsihag.weather_service.services.interfaces.WeatherService;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private RateLimiterService rateLimiterService;

        @MockBean
        private ApiKeyValidationService apiKeyValidationService;

        @MockBean
        private WeatherService weatherService;

        @Test
        public void get_missingAuthHeader_returnsCorrectErrorResponse() throws Exception {
                this.mockMvc.perform(get("/weather?q=London,UK")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                                .andExpect(jsonPath("$.message").value("Missing Api Key in Authorization Header!"));
        }

        @Test
        public void get_invalidAuthHeader_returnsCorrectErrorResponse() throws Exception {
                this.mockMvc.perform(get("/weather?q=London,UK")
                                .header("Authorization", "abc")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                                .andExpect(jsonPath("$.message").value("Invalid Authorization header value!"));
        }

        @Test
        public void get_invalidApiKey_returnsCorrectErrorResponse() throws Exception {
                Mockito.when(apiKeyValidationService.isValidApiKey("XYZ")).thenReturn(false);

                this.mockMvc.perform(get("/weather?q=London,UK")
                                .header("Authorization", "Bearer XYZ")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                                .andExpect(jsonPath("$.message").value("Invalid Api Key provided!"));
        }

        @Test
        public void get_rateLimitExceeded_returnsCorrectErrorResponse() throws Exception {
                Mockito.when(apiKeyValidationService.isValidApiKey("API_KEY_1")).thenReturn(true);
                Mockito.when(rateLimiterService.isRequestAllowed(Mockito.anyString())).thenReturn(false);

                this.mockMvc.perform(get("/weather?q=London,UK")
                                .header("Authorization", "Bearer API_KEY_1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isTooManyRequests())
                                .andExpect(jsonPath("$.code").value(429))
                                .andExpect(jsonPath("$.status").value("429 TOO_MANY_REQUESTS"))
                                .andExpect(jsonPath("$.message")
                                                .value("Rate limit exceeded. Please wait before making additional requests."));
        }

        @Test
        public void get_validAuthHeaderAndRateNotExceeded_returnsWeatherData()
                        throws Exception, WeatherDataUnavailableException {
                Mockito.when(apiKeyValidationService.isValidApiKey("API_KEY_1")).thenReturn(true);
                Mockito.when(rateLimiterService.isRequestAllowed(Mockito.anyString())).thenReturn(true);
                WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO();
                weatherResponseDTO.setSingleWeatherDescription("mock weather description");
                Mockito.when(weatherService.getWeatherData("London,UK")).thenReturn(weatherResponseDTO);

                this.mockMvc.perform(get("/weather?q=London,UK")
                                .header("Authorization", "Bearer API_KEY_1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.weather").value("mock weather description"))
                                .andExpect(status().isOk());
        }

        @Test
        public void get_weatherDataThrowsHttpClientErrorException_returnsCorrectErrorResponse()
                        throws Exception, WeatherDataUnavailableException {
                Mockito.when(apiKeyValidationService.isValidApiKey("API_KEY_1")).thenReturn(true);
                Mockito.when(rateLimiterService.isRequestAllowed(Mockito.anyString())).thenReturn(true);
                Mockito.when(weatherService.getWeatherData("London,UK"))
                                .thenThrow(new HttpClientErrorException(HttpStatusCode.valueOf(404),
                                                "Not Found: \"{\"cod\":\"404\",\"message\":\"city not found\"}\""));

                this.mockMvc.perform(get("/weather?q=London,UK")
                                .header("Authorization", "Bearer API_KEY_1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value(404))
                                .andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
                                .andExpect(jsonPath("$.message")
                                                .value("404 Not Found: \"{\"cod\":\"404\",\"message\":\"city not found\"}\""));
        }

        @Test
        public void get_weatherDataThrowsWeatherDataUnavailableException_returnsCorrectErrorResponse()
                        throws Exception, WeatherDataUnavailableException {
                Mockito.when(apiKeyValidationService.isValidApiKey("API_KEY_1")).thenReturn(true);
                Mockito.when(rateLimiterService.isRequestAllowed(Mockito.anyString())).thenReturn(true);
                Mockito.when(weatherService.getWeatherData("London,UK"))
                                .thenThrow(new WeatherDataUnavailableException(
                                                "Weather data not available. Please try again later!"));

                this.mockMvc.perform(get("/weather?q=London,UK")
                                .header("Authorization", "Bearer API_KEY_1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value(404))
                                .andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
                                .andExpect(jsonPath("$.message")
                                                .value("Weather data not available. Please try again later!"));
        }

}
