package com.surofu.madeinrussia.core.model.session;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SessionDeviceType implements Serializable {
    private String deviceType;

    private SessionDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public static SessionDeviceType of(String deviceType) {
        return new SessionDeviceType(deviceType);
    }
}
