package com.allitov.tasktracker.web.controller;

import com.allitov.tasktracker.model.service.UserService;
import com.allitov.tasktracker.web.dto.request.UserRequest;
import com.allitov.tasktracker.web.dto.response.UserListResponse;
import com.allitov.tasktracker.web.dto.response.UserResponse;
import com.allitov.tasktracker.web.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public Mono<ResponseEntity<UserListResponse>> getAll() {
        return userService.findAll()
                .collectList()
                .map(userMapper::userListToUserListResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getById(@PathVariable("id") String id) {
        return userService.findById(id)
                .map(userMapper::userToResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> create(@Valid @RequestBody UserRequest request) {
        return userService.create(userMapper.requestToUser(request))
                .map(u -> ResponseEntity.created(URI.create("/api/v1/user/" + u.getId())).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateById(@PathVariable("id") String id,
                                                 @Valid @RequestBody UserRequest request) {
        return userService.update(userMapper.requestToUser(id, request))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String id) {
        return userService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
