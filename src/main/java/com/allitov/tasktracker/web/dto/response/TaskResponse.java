package com.allitov.tasktracker.web.dto.response;

import com.allitov.tasktracker.model.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    @Schema(example = "1")
    private String id;

    @Schema(example = "Task name")
    private String name;

    @Schema(example = "Task description")
    private String description;

    @Schema(example = "1970-01-01T00:00:00Z")
    private Instant createdAt;

    @Schema(example = "1970-01-01T00:00:00Z")
    private Instant updatedAt;

    @Schema(example = "TODO")
    private Task.TaskStatus status;

    private UserResponse author;

    private UserResponse assignee;

    private List<UserResponse> observers = new ArrayList<>();
}
