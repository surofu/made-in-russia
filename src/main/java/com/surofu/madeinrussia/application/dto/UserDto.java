package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.userRole.UserRole;
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
        description = "Represents a user STO"
)
public final class UserDto implements Serializable {

    @Schema(
            description = "Unique identifier of the user",
            example = "12345"
    )
    private Long id;

    @Schema(
            description = "User's role with permissions",
            implementation = UserRole.class
    )
    private String role;

    @Schema(
            description = "User's email address",
            example = "user@example.com",
            format = "email"
    )
    private String email;

    @Schema(
            description = "Unique username for authentication",
            example = "john_doe",
            maxLength = 255
    )
    private String login;

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

    @Schema(
            description = "Timestamp of user's last successful login",
            example = "2023-09-01T08:45:12Z",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime lastLoginDate;

    @Schema(hidden = true)
    public static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .role(user.getRole().name().split("ROLE_")[1])
                .login(user.getLogin().getLogin())
                .email(user.getEmail().getEmail())
                .registrationDate(user.getRegistrationDate().getRegistrationDate())
                .lastModificationDate(user.getLastModificationDate().getLastModificationDate())
                .build();
    }
}
