package com.allitov.tasktracker.web.dto.request;

import com.allitov.tasktracker.error.ExceptionMessage;
import com.allitov.tasktracker.model.entity.RoleType;
import com.allitov.tasktracker.web.validation.ValuesOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
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

    @NotBlank(message = ExceptionMessage.USER_INVALID_PASSWORD)
    @Schema(example = "12345")
    private String password;

    @NotNull(message = ExceptionMessage.USER_NULL_ROLES)
    @ValuesOfEnum(enumClass = RoleType.class, message = ExceptionMessage.USER_INVALID_ROLES)
    @Schema(example = "[\"USER\", \"MANAGER\"]", allowableValues = {"USER", "MANAGER"})
    private Set<String> roles;
}
