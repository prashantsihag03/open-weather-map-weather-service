package com.prashantsihag.weather_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(InvalidAuthorizationHeaderException.class)
        public ResponseEntity<ErrorResponse> handleInvalidAuthorizationHeaderException(
                        InvalidAuthorizationHeaderException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                                ex.getMessage(), request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(InvalidApiKeyException.class)
        public ResponseEntity<ErrorResponse> handleInvalidApiKeyException(
                        InvalidApiKeyException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                                ex.getMessage(), request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(RateLimitException.class)
        public ResponseEntity<ErrorResponse> handleRateLimitException(
                        RateLimitException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS,
                                ex.getMessage(), request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
        }

        @ExceptionHandler(HttpClientErrorException.class)
        public ResponseEntity<ErrorResponse> handleHttpClientErrorException(
                        HttpClientErrorException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                ex.getStatusCode(),
                                ex.getMessage(), request.getDescription(false));
                return new ResponseEntity<>(errorResponse, ex.getStatusCode());
        }

        @ExceptionHandler(WeatherDataUnavailableException.class)
        public ResponseEntity<ErrorResponse> handleWeatherDataUnavailableException(
                        WeatherDataUnavailableException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage(), request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

}
