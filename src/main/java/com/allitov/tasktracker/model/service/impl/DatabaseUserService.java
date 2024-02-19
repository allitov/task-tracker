package com.allitov.tasktracker.model.service.impl;

import com.allitov.tasktracker.error.EntityNotFoundException;
import com.allitov.tasktracker.error.ExceptionMessage;
import com.allitov.tasktracker.model.entity.User;
import com.allitov.tasktracker.model.repository.UserRepository;
import com.allitov.tasktracker.model.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DatabaseUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> findById(@NonNull String id) {
        return userRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new EntityNotFoundException(String.format(ExceptionMessage.USER_BY_ID_NOT_FOUND, id))
                        )
                );
    }

    @Override
    public Mono<User> create(@NonNull User user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<User> update(@NonNull User user) {
        return findById(user.getId()).flatMap(
                foundUser -> !foundUser.equals(user) ? userRepository.save(user) : Mono.just(foundUser)
        );
    }

    @Override
    public Mono<Void> deleteById(@NonNull String id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Flux<User> findAllByIdsIn(@NonNull Iterable<String> ids) {
        return userRepository.findAllById(ids);
    }
}
