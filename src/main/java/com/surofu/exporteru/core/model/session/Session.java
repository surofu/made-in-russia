package com.surofu.exporteru.core.model.session;

import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.user.User;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
public final class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session session)) return false;
        return id != null && id.equals(session.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static Session of(SessionInfo sessionInfo, User user, Session session) {
        UserAgent userAgent = sessionInfo.getUserAgent();
        SessionDeviceId sessionDeviceId = sessionInfo.getDeviceId();
        SessionIpAddress sessionIpAddress = sessionInfo.getIpAddress();
        String rawDeviceType = userAgent.getOperatingSystem().getDeviceType().getName();
        SessionDeviceType sessionDeviceType = SessionDeviceType.of(rawDeviceType);
        String rawBrowserName = userAgent.getBrowser().getName();
        SessionBrowser sessionBrowser = SessionBrowser.of(rawBrowserName);
        String rawOsName = userAgent.getOperatingSystem().getName();
        SessionOs sessionOs = SessionOs.of(rawOsName);

        session.setUser(user);
        session.setDeviceId(sessionDeviceId);
        session.setDeviceType(sessionDeviceType);
        session.setBrowser(sessionBrowser);
        session.setOs(sessionOs);
        session.setIpAddress(sessionIpAddress);
        return session;
    }
}
