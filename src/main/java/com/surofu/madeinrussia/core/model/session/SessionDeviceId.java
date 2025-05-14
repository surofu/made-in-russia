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
public final class SessionDeviceId implements Serializable {

    @Column(nullable = false)
    private String deviceId;

    private SessionDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public static SessionDeviceId of(String deviceId) {
        return new SessionDeviceId(deviceId);
    }
}
