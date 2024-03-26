package com.allitov.tasktracker.web.controller;

import com.allitov.tasktracker.model.service.TaskService;
import com.allitov.tasktracker.web.dto.request.ChangeTaskRequest;
import com.allitov.tasktracker.web.dto.request.CreateTaskRequest;
import com.allitov.tasktracker.web.dto.response.ErrorResponse;
import com.allitov.tasktracker.web.dto.response.TaskListResponse;
import com.allitov.tasktracker.web.dto.response.TaskResponse;
import com.allitov.tasktracker.web.mapper.TaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/task")
@RequiredArgsConstructor
@Tag(name = "Task controller", description = "Task API version 2.0")
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @Operation(
            summary = "Get all tasks",
            description = "Get all tasks. Returns a list of tasks. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and tasks list if everything completed successfully",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = TaskListResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 401 and error message if user is not authorized",
                    responseCode = "401",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 403 and error message if user has no required authorities",
                    responseCode = "403",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @GetMapping
    public Mono<ResponseEntity<TaskListResponse>> getAll() {
        return taskService.findAll()
                .collect(Collectors.toList())
                .map(taskMapper::taskListToTaskListResponse)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Get task by id",
            description = "Get task by id. Returns task with requested id. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
            parameters = {
                    @Parameter(name = "id", example = "1")
            },
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and task if everything completed successfully",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = TaskResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 401 and error message if user is not authorized",
                    responseCode = "401",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 403 and error message if user has no required authorities",
                    responseCode = "403",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 404 and error message if task with requested id was not found",
                    responseCode = "404",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getById(@PathVariable("id") String id) {
        return taskService.findById(id)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Create task",
            description = "Create task. Returns status 201 and created task location. " +
                    "Requires any of the authorities: ['MANAGER']",
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 201 and created task location if everything completed successfully",
                    responseCode = "201",
                    headers = {
                            @Header(name = "Location", description = "Created task location")
                    }
            ),
            @ApiResponse(
                    description = "Returns status 400 and error message if request has invalid values",
                    responseCode = "400",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 401 and error message if user is not authorized",
                    responseCode = "401",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 403 and error message if user has no required authorities",
                    responseCode = "403",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 404 and error message if request has invalid user id",
                    responseCode = "404",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @PostMapping
    public Mono<ResponseEntity<Void>> create(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.create(taskMapper.createRequestToTask(request))
                .map(createdTask ->
                        ResponseEntity.created(URI.create("/api/v2/user/" + createdTask.getId())).build()
                );
    }

    @Operation(
            summary = "Update task by id",
            description = "Update task by id. Returns status 204. " +
                    "Requires any of the authorities: ['MANAGER']",
            parameters = {
                    @Parameter(name = "id", example = "1")
            },
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 204 if everything completed successfully",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Returns status 400 and error message if request has invalid values",
                    responseCode = "400",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 401 and error message if user is not authorized",
                    responseCode = "401",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 403 and error message if user has no required authorities",
                    responseCode = "403",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 404 and error message " +
                            "if task or user with requested id was not found",
                    responseCode = "404",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateById(@PathVariable("id") String id,
                                                 @Valid @RequestBody ChangeTaskRequest request) {
        return taskService.update(taskMapper.changeReuqestToTask(id, request))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(
            summary = "Add observer by id",
            description = "Add observer by id. Returns status 200 and updated task. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
            parameters = {
                    @Parameter(name = "id", example = "1"),
                    @Parameter(name = "observerId", example = "1")
            },
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and updated task if everything completed successfully",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = TaskResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 401 and error message if user is not authorized",
                    responseCode = "401",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 403 and error message if user has no required authorities",
                    responseCode = "403",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 404 and error message " +
                            "if task or user with requested id was not found",
                    responseCode = "404",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @PatchMapping("/{id}/add-observer")
    public Mono<ResponseEntity<TaskResponse>> addObserverById(@PathVariable("id") String taskId,
                                                              @RequestParam("observerId") String observerId) {
        return taskService.addObserverById(taskId, observerId)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Remove observer by id",
            description = "Remove observer by id. Returns status 200 and updated task. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
            parameters = {
                    @Parameter(name = "id", example = "1"),
                    @Parameter(name = "observerId", example = "1")
            },
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and updated task if everything completed successfully",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = TaskResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 401 and error message if user is not authorized",
                    responseCode = "401",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 403 and error message if user has no required authorities",
                    responseCode = "403",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 404 and error message " +
                            "if task or user with requested id was not found",
                    responseCode = "404",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @PatchMapping("/{id}/remove-observer")
    public Mono<ResponseEntity<TaskResponse>> removeObserverById(@PathVariable("id") String taskId,
                                                                 @RequestParam("observerId") String observerId) {
        return taskService.removeObserverById(taskId, observerId)
                .map(taskMapper::taskToResponse)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Delete task by id",
            description = "Delete task by id. Returns status 204. " +
                    "Requires any of the authorities: ['MANAGER']",
            parameters = {
                    @Parameter(name = "id", example = "1")
            },
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 204 if everything completed successfully",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Returns status 401 and error message if user is not authorized",
                    responseCode = "401",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 403 and error message if user has no required authorities",
                    responseCode = "403",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String id) {
        return taskService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
