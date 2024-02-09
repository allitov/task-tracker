package com.allitov.tasktracker.model.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService<T, ID> {

    Flux<T> findAll();

    Mono<T> findById(Mono<ID> id);

    Mono<T> create(Mono<T> user);

    Mono<T> updateById(Mono<ID> id, Mono<T> user);

    Mono<Void> deleteById(Mono<ID> id);
}
