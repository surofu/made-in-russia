package com.surofu.exporteru.application.command.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ForceUpdateUserCommand",
        description = "DTO for administrative user profile updates",
        requiredProperties = {"email", "login", "phoneNumber", "region"}
)
public record ForceUpdateUserCommand(
        @Schema(
                description = "User's email address",
                example = "user@example.com",
                pattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
                maxLength = 255
        )
        String email,

        @Schema(
                description = "User's login username",
                example = "john_doe",
                minLength = 3,
                maxLength = 50,
                pattern = "^[a-zA-Z0-9_]+$"
        )
        String login,

        @Schema(
                description = "User's phone number in international format",
                example = "+1234567890",
                nullable = true,
                pattern = "^\\+[1-9]\\d{1,14}$"
        )
        String phoneNumber,

        @Schema(
                description = "User's geographical region",
                example = "North America",
                nullable = true,
                maxLength = 100
        )
        String region
) {}
