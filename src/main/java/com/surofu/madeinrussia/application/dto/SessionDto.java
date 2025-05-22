package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.session.Session;
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
public final class SessionDto implements Serializable {

    @Schema(
            description = "Unique identifier of the session",
            example = "12345",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

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
            example = "192.168. 123.132",
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
    public static SessionDto of(Session session) {
        return SessionDto.builder()
                .id(session.getId())
                .deviceId(session.getDeviceId().getDeviceId())
                .deviceType(session.getDeviceType().getDeviceType())
                .browser(session.getBrowser().getBrowser())
                .os(session.getOs().getOs())
                .ipAddress(session.getIpAddress().getIpAddress())
                .creationDate(session.getCreationDate().getCreationDate())
                .lastModificationDate(session.getLastModificationDate().getLastModificationDate())
                .lastLoginDate(session.getLastLoginDate().getLastLoginDate())
                .build();
    }
}
