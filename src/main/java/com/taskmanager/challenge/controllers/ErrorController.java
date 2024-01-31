package com.taskmanager.challenge.controllers;

import com.taskmanager.challenge.exceptions.NotAuthorizedException;
import com.taskmanager.challenge.exceptions.NotFoundException;
import com.taskmanager.challenge.model.response.CountingTaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CountingTaskResponse> handleNotFound() {
        log.warn("Entity not found");
        return new ResponseEntity<>(CountingTaskResponse.builder().errorMessage("Given resource is not found")
                .build(),HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<CountingTaskResponse> handleNotAuthorized() {
        log.warn("Not authorized");
        return new ResponseEntity<>(CountingTaskResponse.builder().errorMessage("Not authorized")
                .build(),HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CountingTaskResponse> handleInternalError(Exception e) {
        log.error("Unhandled Exception in Controller", e);
        return new ResponseEntity<>(CountingTaskResponse.builder().errorMessage("Internal error while processing the" +
                        " request")
                .build(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
