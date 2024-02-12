package com.allitov.tasktracker.model.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService<T, ID> {

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Mono<T> create(T user);

    Mono<T> update(T user);

    Mono<Void> deleteById(ID id);
}
