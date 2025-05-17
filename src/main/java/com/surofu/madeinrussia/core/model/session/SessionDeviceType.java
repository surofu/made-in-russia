package com.surofu.madeinrussia.core.model.session;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SessionDeviceType implements Serializable {

    @Column(nullable = false)
    private String deviceType;

    private SessionDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public static SessionDeviceType of(String deviceType) {
        return new SessionDeviceType(deviceType);
    }

    @Override
    public String toString() {
        return deviceType;
    }
}
