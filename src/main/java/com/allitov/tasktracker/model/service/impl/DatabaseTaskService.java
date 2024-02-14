package com.allitov.tasktracker.model.service.impl;

import com.allitov.tasktracker.error.EntityNotFoundException;
import com.allitov.tasktracker.error.ExceptionMessage;
import com.allitov.tasktracker.model.entity.Task;
import com.allitov.tasktracker.model.repository.TaskRepository;
import com.allitov.tasktracker.model.service.TaskService;
import com.allitov.tasktracker.model.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatabaseTaskService implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    @Override
    public Flux<Task> findAll() {
        return taskRepository.findAll()
                .flatMap(this::zipStreams);
    }

    @Override
    public Mono<Task> findById(@NonNull String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new EntityNotFoundException(String.format(ExceptionMessage.TASK_BY_ID_NOT_FOUND, id))
                        )
                )
                .flatMap(this::zipStreams);
    }

    @Override
    public Mono<Task> create(@NonNull Task task) {
        return zipStreams(task)
                .flatMap(taskToSave -> {
                    Instant now = Instant.now();
                    taskToSave.setCreatedAt(now);
                    taskToSave.setUpdatedAt(now);
                    return taskRepository.save(task);
                });
    }

    @Override
    public Mono<Task> update(@NonNull Task task) {
        return findById(task.getId()).flatMap(foundTask -> {
            if (!foundTask.equals(task)) {
                foundTask.setName(task.getName());
                foundTask.setDescription(task.getDescription());
                foundTask.setStatus(task.getStatus());
                foundTask.setAssignee(task.getAssignee());
                foundTask.setUpdatedAt(Instant.now());
                return taskRepository.save(foundTask);
            }
            return Mono.just(foundTask);
        });
    }

    @Override
    public Mono<Task> addObserverById(@NonNull String taskId, @NonNull String observerId) {
        return Mono
                .zip(
                        findById(taskId),
                        userService.findById(observerId),
                        (task, user) -> {
                            task.addObserverId(user.getId());
                            task.setUpdatedAt(Instant.now());
                            return task;
                    }
                )
                .flatMap(taskRepository::save)
                .flatMap(this::zipStreams);
    }

    @Override
    public Mono<Task> removeObserverById(String taskId, String observerId) {
        return Mono
                .zip(
                        findById(taskId),
                        userService.findById(observerId),
                        (task, user) -> {
                            task.removeObserverId(user.getId());
                            task.setUpdatedAt(Instant.now());
                            return task;
                        }
                )
                .flatMap(taskRepository::save)
                .flatMap(this::zipStreams);
    }

    @Override
    public Mono<Void> deleteById(@NonNull String id) {
        return taskRepository.deleteById(id);
    }

    private Mono<Task> zipStreams(Task task) {
        return Mono
                .zip(
                    Mono.just(task),
                    userService.findById(task.getAuthorId()),
                    userService.findById(task.getAssigneeId()),
                    userService.findAllByIdsIn(task.getObserverIds()).collect(Collectors.toSet())
                )
                .map(tuple -> {
                    Task result = tuple.getT1();
                    result.setAuthor(tuple.getT2());
                    result.setAssignee(tuple.getT3());
                    result.setObservers(tuple.getT4());
                    return result;
                });
    }
}
