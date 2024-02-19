package com.allitov.tasktracker.web.dto.request;

import com.allitov.tasktracker.error.ExceptionMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = ExceptionMessage.USER_INVALID_USERNAME)
    @Schema(example = "Username")
    private String username;

    @NotBlank(message = ExceptionMessage.USER_NULL_EMAIL)
    @Email(message = ExceptionMessage.USER_INVALID_EMAIL)
    @Schema(example = "example@email.com")
    private String email;
}
