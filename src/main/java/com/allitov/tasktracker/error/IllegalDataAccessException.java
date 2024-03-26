package com.allitov.tasktracker.error;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IllegalDataAccessException extends RuntimeException {

    public IllegalDataAccessException(String message) {
        super(message);
    }
}
