package com.allitov.tasktracker.web.dto.response;

import com.allitov.tasktracker.model.entity.Task;
import com.allitov.tasktracker.model.entity.User;
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

    private String id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private Task.TaskStatus status;

    private User author;

    private User assignee;

    private List<User> observers = new ArrayList<>();
}
