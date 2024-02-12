package com.allitov.tasktracker.model.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService<T, ID> {

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Mono<T> create(T task);

    Mono<T> updateById(ID id, T task);

    Mono<T> addObserverById(ID taskId, ID observerId);

    Mono<Void> deleteById(ID id);
}
