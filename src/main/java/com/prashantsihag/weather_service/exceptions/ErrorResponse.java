package com.prashantsihag.weather_service.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    private int code;
    private String status;
    private Object message;
    private String path;
    private String stackTrace;

    public ErrorResponse() {
        timestamp = new Date();
    }

    public ErrorResponse(HttpStatusCode httpStatus, Object message) {
        this();

        this.code = httpStatus.value();
        this.status = httpStatus.toString();
        this.message = message;
    }

    public ErrorResponse(HttpStatusCode httpStatus, Object message, String path) {
        this(httpStatus, message);
        this.path = path;
    }

    public ErrorResponse(HttpStatusCode status2, Object message, String path, String stackTrace) {
        this(status2, message, path);
        this.stackTrace = stackTrace;
    }
}