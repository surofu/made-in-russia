package com.surofu.exporteru.application.command.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ChangeUserRoleCommand",
        description = "DTO for changing user's role",
        requiredProperties = {"role"}
)
public record ChangeUserRoleCommand(
        @Schema(
                description = "New role to assign to the user",
                example = "ROLE_ADMIN",
                allowableValues = {"ROLE_ADMIN", "ROLE_USER", "ROLE_VENDOR"},
                minLength = 1,
                maxLength = 50
        )
        String role
) {}
