package com.surofu.madeinrussia.core.model.session;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SessionDeviceName implements Serializable {
    private String deviceName;

    private SessionDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public static SessionDeviceName of(String deviceName) {
        return new SessionDeviceName(deviceName);
    }
}
