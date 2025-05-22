package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.session.SessionWithUser;
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
        name = "UserSession",
        description = "Represents a user session DTO"
)
public final class SessionWithUserDto implements Serializable {

    @Schema(
            description = "Unique identifier of the session",
            example = "12345",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "User Dto of the session",
            implementation = UserDto.class
    )
    private UserDto user;

    @Schema(
            description = "Session unique device id",
            example = "af38c51bbcd3af6bd77bd8ac23c1d752b8f22e9b57bbc8f51882eafb97f076b5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String deviceId;

    @Schema(
            description = "User's unique username for authentication",
            example = "Mobile",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String deviceType;

    @Schema(
            description = "Session browser name",
            example = "Chrome",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String browser;

    @Schema(
            description = "Session operation system name",
            example = "Android 6.x",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String os;

    @Schema(
            description = "Session ip address",
            example = "192.168.123.132",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String ipAddress;

    @Schema(
            description = "Timestamp when session was created",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when session was last modified",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(
            description = "Timestamp when user was last login",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastLoginDate;

    @Schema(hidden = true)
    public static SessionWithUserDto of(SessionWithUser sessionWithUser) {
        return SessionWithUserDto.builder()
                .id(sessionWithUser.getId())
                .user(UserDto.of(sessionWithUser.getUser()))
                .deviceId(sessionWithUser.getDeviceId().getDeviceId())
                .deviceType(sessionWithUser.getDeviceType().getDeviceType())
                .browser(sessionWithUser.getBrowser().getBrowser())
                .os(sessionWithUser.getOs().getOs())
                .ipAddress(sessionWithUser.getIpAddress().getIpAddress())
                .creationDate(sessionWithUser.getCreationDate().getCreationDate())
                .lastModificationDate(sessionWithUser.getLastModificationDate().getLastModificationDate())
                .lastLoginDate(sessionWithUser.getLastLoginDate().getLastLoginDate())
                .build();
    }
}
