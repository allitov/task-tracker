package com.allitov.tasktracker.web.dto.request;

import com.allitov.tasktracker.error.ExceptionMessage;
import com.allitov.tasktracker.model.entity.Task;
import com.allitov.tasktracker.web.validation.ValuesOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeTaskRequest {

    @NotBlank(message = ExceptionMessage.TASK_INVALID_NAME)
    @Schema(example = "Task name")
    private String name;

    @NotNull(message = ExceptionMessage.TASK_NULL_DESCRIPTION)
    @Schema(example = "Task description")
    private String description;

    @NotNull(message = ExceptionMessage.TASK_NULL_STATUS)
    @ValuesOfEnum(enumClass = Task.TaskStatus.class, message = ExceptionMessage.TASK_INVALID_STATUS)
    @Schema(example = "TODO", allowableValues = {"TODO", "IN_PROGRESS", "DONE"})
    private String status;

    @NotNull(message = ExceptionMessage.TASK_NULL_ASSIGNEE_ID)
    @Schema(example = "1")
    private String assigneeId;
}
