package com.allitov.tasktracker.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessage {

    public final String USER_BY_ID_NOT_FOUND = "User with id '%s' not found";

    public final String TASK_BY_ID_NOT_FOUND = "Task with id '%s' not found";
}
