package com.allitov.tasktracker.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessage {

    public final String USER_BY_ID_NOT_FOUND = "User with id '%s' not found";

    public final String USER_INVALID_USERNAME = "Username must not be blank";

    public final String USER_NULL_EMAIL = "Email must be specified";

    public final String USER_INVALID_EMAIL = "Invalid email format";

    public final String TASK_BY_ID_NOT_FOUND = "Task with id '%s' not found";

    public final String TASK_INVALID_NAME = "Task name must not be blank";

    public final String TASK_NULL_DESCRIPTION = "Task description must be specified";

    public final String TASK_NULL_STATUS = "Task status must be specified";

    public final String TASK_INVALID_STATUS = "Task status must be any of [TODO, IN_PROGRESS, DONE]";

    public final String TASK_NULL_AUTHOR_ID = "Task author id must be specified";

    public final String TASK_NULL_ASSIGNEE_ID = "Task assignee id must be specified";

    public final String TASK_NULL_OBSERVER_IDS = "Task observer ids must be specified";
}
