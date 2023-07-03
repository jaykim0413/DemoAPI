package com.demo.api.advice;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.demo.api.exception.ErrorMessage;
import com.demo.api.exception.ResourceNotFoundException;

@RestControllerAdvice
public class PostControllerAdvice {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ErrorMessage resourseNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                exception.getMessage(),
                request.getDescription(false));
    }
}
