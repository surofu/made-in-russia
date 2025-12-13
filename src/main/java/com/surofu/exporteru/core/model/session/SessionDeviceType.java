package com.surofu.exporteru.core.model.session;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
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

    @Column(name = "device_type", nullable = false, updatable = false)
    private String value;

    private SessionDeviceType(String deviceType) {
        if (deviceType == null || deviceType.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.session.device_type.empty");
        }

        if (deviceType.length() > 255) {
            throw new LocalizedValidationException("validation.session.device_type.max_length");
        }

        this.value = deviceType;
    }

    public static SessionDeviceType of(String deviceType) {
        return new SessionDeviceType(deviceType);
    }

    @Override
    public String toString() {
        return value;
    }
}
