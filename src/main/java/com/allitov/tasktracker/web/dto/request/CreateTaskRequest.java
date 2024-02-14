package com.allitov.tasktracker.web.dto.request;

import com.allitov.tasktracker.model.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    private String name;

    private String description;

    private Task.TaskStatus status;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;
}
