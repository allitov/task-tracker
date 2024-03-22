package com.allitov.tasktracker.web.controller;

import com.allitov.tasktracker.model.service.UserService;
import com.allitov.tasktracker.web.dto.request.UserRequest;
import com.allitov.tasktracker.web.dto.response.ErrorResponse;
import com.allitov.tasktracker.web.dto.response.UserListResponse;
import com.allitov.tasktracker.web.dto.response.UserResponse;
import com.allitov.tasktracker.web.mapper.UserMapper;
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

@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
@Tag(name = "User controller", description = "User API version 2.0")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @Operation(
            summary = "Get all users",
            description = "Get all users. Returns a list of users. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and users list if everything completed successfully",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = UserListResponse.class),
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
    public Mono<ResponseEntity<UserListResponse>> getAll() {
        return userService.findAll()
                .collectList()
                .map(userMapper::userListToUserListResponse)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Get user by id",
            description = "Get user by id. Returns user with requested id. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
            parameters = {
                    @Parameter(name = "id", example = "1")
            },
            security = @SecurityRequirement(name = "Basic authorization")
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and user if everything completed successfully",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = UserResponse.class),
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
                    description = "Returns status 404 and error message if user with requested id was not found",
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
    public Mono<ResponseEntity<UserResponse>> getById(@PathVariable("id") String id) {
        return userService.findById(id)
                .map(userMapper::userToResponse)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Create user",
            description = "Create user. Returns status 201 and created user location"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 201 and created user location if everything completed successfully",
                    responseCode = "201",
                    headers = {
                            @Header(name = "Location", description = "Created user location")
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
            )
    })
    @PostMapping
    public Mono<ResponseEntity<Void>> create(@Valid @RequestBody UserRequest request) {
        return userService.create(userMapper.requestToUser(request))
                .map(u -> ResponseEntity.created(URI.create("/api/v2/user/" + u.getId())).build());
    }

    @Operation(
            summary = "Update user by id",
            description = "Update user by id. Returns status 204. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
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
                    description = "Returns status 404 and error message if user with requested id was not found",
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
                                                 @Valid @RequestBody UserRequest request) {
        return userService.update(userMapper.requestToUser(id, request))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(
            summary = "Delete user by id",
            description = "Delete user by id. Returns status 204. " +
                    "Requires any of the authorities: ['USER', 'MANAGER']",
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
        return userService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
