package com.allitov.tasktracker.model.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService<T, ID> {

    Flux<T> findAll();

    Mono<T> findById(Mono<ID> id);

    Mono<T> create(Mono<T> task);

    Mono<T> updateById(Mono<ID> id, Mono<T> task);

    Mono<T> addObserverById(Mono<ID> taskId, Mono<ID> observerId);

    Mono<Void> deleteById(Mono<ID> id);
}
