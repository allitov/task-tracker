package com.allitov.tasktracker.web.controller;

import com.allitov.tasktracker.error.EntityNotFoundException;
import com.allitov.tasktracker.error.IllegalDataAccessException;
import com.allitov.tasktracker.web.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    @NonNull
    @SneakyThrows
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable throwable) {
        switch (throwable) {
            case WebExchangeBindException validationEx -> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                ErrorResponse responseBody = new ErrorResponse(getValidationErrors(validationEx));
                return writeResponse(exchange, objectMapper.writeValueAsBytes(responseBody));
            }
            case EntityNotFoundException notFoundEx -> {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                ErrorResponse responseBody = new ErrorResponse(notFoundEx.getMessage());
                return writeResponse(exchange, objectMapper.writeValueAsBytes(responseBody));
            }
            case IllegalDataAccessException dataAccessEx -> {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                ErrorResponse responseBody = new ErrorResponse(dataAccessEx.getMessage());
                return writeResponse(exchange, objectMapper.writeValueAsBytes(responseBody));
            }
            default -> {
                return Mono.error(throwable);
            }
        }
    }

    private String getValidationErrors(WebExchangeBindException e) {
        return e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, byte[] responseBytes) {
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(responseBytes))
        );
    }
}
