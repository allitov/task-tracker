package com.allitov.tasktracker.web.dto.response;

import com.allitov.tasktracker.model.entity.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Schema(example = "1")
    private String id;

    @Schema(example = "Username")
    private String username;

    @Schema(example = "example@email.com")
    private String email;

    @Schema(example = "[\"USER\"]")
    private Set<RoleType> roles;
}
