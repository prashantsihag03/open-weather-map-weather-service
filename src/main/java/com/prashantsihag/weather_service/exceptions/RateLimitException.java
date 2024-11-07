package com.prashantsihag.weather_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RateLimitException extends Throwable {
    private String message;
}
