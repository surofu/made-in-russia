package com.surofu.madeinrussia.core.model.session;

import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
@NamedEntityGraph(
        name = "session.with-user",
        attributeNodes = @NamedAttributeNode("user")
)
public final class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

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
