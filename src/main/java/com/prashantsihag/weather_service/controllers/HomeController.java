package com.prashantsihag.weather_service.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping()
    public ResponseEntity<Map<String, String>> get() {

        return ResponseEntity.ok(Map.of("msg",
                "Welcome to Weather Service. Please visit and provide an API key to Authorisation header as \"Bearer <API_KEY>\" to http://localhost:8080/weather?q=London,UK"));
    }
}
