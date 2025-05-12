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
        description = "Represents a user DTO"
)
public final class UserDto implements Serializable {

    @Schema(
            description = "Unique identifier of the user",
            example = "12345"
    )
    private Long id;

    @Schema(
            description = "User's role with permissions"
    )
    private String role;

    @Schema(
            description = "User's email address for authentication",
            example = "user@example.com",
            format = "email"
    )
    private String email;

    @Schema(
            description = "User's unique username for authentication",
            example = "john_doe",
            maxLength = 255
    )
    private String login;

    @Schema(
            description = "User's unique phone number for authentication",
            example = "+79123456789"
    )
    private String phoneNumber;

    @Schema(
            description = "User's region",
            example = "Russia"
    )
    private String region;

    @Schema(
            description = "Timestamp when user was registered",
            example = "2023-07-15T10:30:00Z",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime registrationDate;

    @Schema(
            description = "Timestamp when user data was last modified",
            example = "2023-08-20T14:15:30Z",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .role(user.getRole().getName())
                .login(user.getLogin().getLogin())
                .email(user.getEmail().getEmail())
                .registrationDate(user.getRegistrationDate().getRegistrationDate())
                .lastModificationDate(user.getLastModificationDate().getLastModificationDate())
                .build();
    }
}
