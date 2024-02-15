package com.allitov.tasktracker.web.dto.request;

import com.allitov.tasktracker.error.ExceptionMessage;
import com.allitov.tasktracker.model.entity.Task;
import com.allitov.tasktracker.validation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeTaskRequest {

    @NotBlank(message = ExceptionMessage.TASK_INVALID_NAME)
    private String name;

    @NotNull(message = ExceptionMessage.TASK_NULL_DESCRIPTION)
    private String description;

    @NotNull(message = ExceptionMessage.TASK_NULL_STATUS)
    @ValueOfEnum(enumClass = Task.TaskStatus.class, message = ExceptionMessage.TASK_INVALID_STATUS)
    private String status;

    @NotNull(message = ExceptionMessage.TASK_NULL_ASSIGNEE_ID)
    private String assigneeId;
}
