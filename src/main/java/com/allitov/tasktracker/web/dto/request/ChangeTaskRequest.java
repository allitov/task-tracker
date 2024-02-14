package com.allitov.tasktracker.web.dto.request;

import com.allitov.tasktracker.model.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeTaskRequest {

    private String name;

    private String description;

    private Task.TaskStatus status;

    private String assigneeId;
}
