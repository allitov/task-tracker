package com.allitov.tasktracker.model.service;

import com.allitov.tasktracker.model.entity.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {

    Flux<Task> findAll();

    Mono<Task> findById(String id);

    Mono<Task> create(Task task);

    Mono<Task> update(Task task);

    Mono<Task> addObserverById(String taskId, String observerId);

    Mono<Task> removeObserverById(String taskId, String observerId);

    Mono<Void> deleteById(String id);
}
