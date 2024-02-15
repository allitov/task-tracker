package com.allitov.tasktracker.web.dto.request;

import com.allitov.tasktracker.error.ExceptionMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = ExceptionMessage.USER_INVALID_USERNAME)
    private String username;

    @Email(message = ExceptionMessage.USER_INVALID_EMAIL)
    private String email;
}
