package com.allitov.tasktracker.web.controller;

import com.allitov.tasktracker.model.service.TaskService;
import com.allitov.tasktracker.web.dto.request.ChangeTaskRequest;
import com.allitov.tasktracker.web.dto.request.CreateTaskRequest;
import com.allitov.tasktracker.web.dto.response.TaskListResponse;
import com.allitov.tasktracker.web.dto.response.TaskResponse;
import com.allitov.tasktracker.web.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @GetMapping
    public Mono<ResponseEntity<TaskListResponse>> getAll() {
        return taskService.findAll()
                .collect(Collectors.toList())
                .map(taskMapper::taskListToTaskListResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getById(@PathVariable("id") String id) {
        return taskService.findById(id)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> create(@RequestBody CreateTaskRequest request) {
        return taskService.create(taskMapper.createRequestToTask(request))
                .map(t -> ResponseEntity.created(URI.create("/api/v1/user/" + t.getId())).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateById(@PathVariable("id") String id,
                                                 @RequestBody ChangeTaskRequest request) {
        return taskService.update(taskMapper.changeReuqestToTask(id, request))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PatchMapping("/{id}/add-observer")
    public Mono<ResponseEntity<TaskResponse>> addObserverById(@PathVariable("id") String taskId,
                                                              @RequestParam("id") String observerId) {
        return taskService.addObserverById(taskId, observerId)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/remove-observer")
    public Mono<ResponseEntity<TaskResponse>> removeObserverById(@PathVariable("id") String taskId,
                                                                 @RequestParam("id") String observerId) {
        return taskService.removeObserverById(taskId, observerId)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String id) {
        return taskService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
