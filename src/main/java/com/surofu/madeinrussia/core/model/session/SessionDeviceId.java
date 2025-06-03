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

    @Column(name = "device_id", nullable = false, updatable = false)
    private String value;

    private SessionDeviceId(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID устройства сессии не может быть пустым");
        }

        this.value = deviceId;
    }
 
    public static SessionDeviceId of(String deviceId) {
        return new SessionDeviceId(deviceId);
    }

    @Override
    public String toString() {
        return value;
    }
}
