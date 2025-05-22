package com.surofu.madeinrussia.core.model.session;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "sessions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_session_device_user",
                        columnNames = {"device_id", "user_id"}
                )
        },
        indexes = @Index(columnList = "deviceId")
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private SessionDeviceId deviceId;

    @Embedded
    private SessionDeviceType deviceType;

    @Embedded
    private SessionBrowser browser;

    @Embedded
    private SessionOs os;

    @Embedded
    private SessionIpAddress ipAddress;

    @Embedded
    private SessionCreationDate creationDate;

    @Embedded
    private SessionLastModificationDate lastModificationDate;

    @Embedded
    private SessionLastLoginDate lastLoginDate;
}
