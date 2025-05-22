package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "User",
        description = "Represents user information with authentication details and metadata",
        example = """
                {
                  "id": 12345,
                  "role": "User",
                  "email": "user@example.com",
                  "login": "john_doe",
                  "phoneNumber": "+79123456789",
                  "region": "Moscow, Russia",
                  "registrationDate": "2025-05-04T09:17:20.767615Z",
                  "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                }
                """
)
public final class UserDto implements Serializable {

    @Schema(
            description = "Unique identifier of the user",
            example = "12345",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "User's role with permissions",
            example = "ROLE_CUSTOMER",
            allowableValues = {"ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_CUSTOMER", "ROLE_VENDOR"},
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
            description = "User's geographical region or location",
            example = "Moscow, Russia",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String region;

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

    @Schema(hidden = true)
    public static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .role(user.getRole().getName())
                .login(user.getLogin().getLogin())
                .email(user.getEmail().getEmail())
                .phoneNumber(user.getPhoneNumber().getPhoneNumber())
                .region(user.getRegion().getRegion())
                .registrationDate(user.getRegistrationDate().getRegistrationDate())
                .lastModificationDate(user.getLastModificationDate().getLastModificationDate())
                .build();
    }
}