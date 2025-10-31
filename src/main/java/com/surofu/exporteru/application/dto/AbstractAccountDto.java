package com.surofu.exporteru.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAccountDto implements Serializable {

    @Schema(
            description = "Unique identifier of the user",
            example = "12345",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    private Boolean isEnabled;

    @Schema(
            description = "User's role with permissions",
            example = "ROLE_USER",
            allowableValues = {"ROLE_ADMIN", "ROLE_USER", "ROLE_VENDOR"},
            maxLength = 50,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String role;

    @Schema(
            description = "User's email address for authentication and communication",
            example = "user@example.com",
            format = "email",
            pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(
            description = "User's unique username for authentication and display",
            example = "john_doe_2025",
            pattern = "^[a-zA-Z0-9_\\-.]{3,50}$",
            minLength = 3,
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String login;

    @Schema(
            description = "User's phone number in E.164 format",
            example = "+79123456789",
            pattern = "^\\+[1-9]\\d{1,14}$",
            maxLength = 15,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String phoneNumber;

    @Schema(
            description = "User's avatar url",
            example = "https://media.tenor.com/x8v1oNUOmg4AAAAM/rickroll-roll.gif",
            maxLength = 20_000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String avatarUrl;

    @Schema(
            description = "Timestamp when user account was created",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime registrationDate;

    @Schema(
            description = "Timestamp when user profile was last updated",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;
}
