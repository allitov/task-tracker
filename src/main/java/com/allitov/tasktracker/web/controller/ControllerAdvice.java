package com.allitov.tasktracker.web.controller;

import com.allitov.tasktracker.error.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<Void>> EntityNotFoundExceptionHandler() {
        return Mono.just(ResponseEntity.notFound().build());
    }
}
