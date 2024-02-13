package com.allitov.tasktracker.model.service;

import com.allitov.tasktracker.model.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Flux<User> findAll();

    Mono<User> findById(String id);

    Mono<User> create(User user);

    Mono<User> update(User user);

    Mono<Void> deleteById(String id);

    Flux<User> findAllByIdsIn(Iterable<String> ids);
}
