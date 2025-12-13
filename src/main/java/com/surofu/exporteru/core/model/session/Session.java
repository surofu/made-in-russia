package com.surofu.exporteru.core.model.session;

import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.user.User;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Session session)) {
      return false;
    }
    return Objects.equals(deviceId, session.deviceId) &&
        Objects.equals(deviceType, session.deviceType) &&
        Objects.equals(browser, session.browser) &&
        Objects.equals(os, session.os) &&
        Objects.equals(ipAddress, session.ipAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deviceId, deviceType, browser, os, ipAddress);
  }
}
