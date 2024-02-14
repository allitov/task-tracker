package com.allitov.tasktracker.web.mapper;

import com.allitov.tasktracker.model.entity.Task;
import com.allitov.tasktracker.web.dto.request.ChangeTaskRequest;
import com.allitov.tasktracker.web.dto.request.CreateTaskRequest;
import com.allitov.tasktracker.web.dto.response.TaskListResponse;
import com.allitov.tasktracker.web.dto.response.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task createRequestToTask(CreateTaskRequest request);

    Task changeRequestToTask(ChangeTaskRequest request);

    default Task changeReuqestToTask(String id, ChangeTaskRequest request) {
        Task task = changeRequestToTask(request);
        task.setId(id);

        return task;
    }

    TaskResponse taskToResponse(Task task);

    List<TaskResponse> taskListToResponseList(List<Task> tasks);

    default TaskListResponse taskListToTaskListResponse(List<Task> tasks) {
        TaskListResponse response = new TaskListResponse();
        response.setTasks(taskListToResponseList(tasks));

        return response;
    }
}
