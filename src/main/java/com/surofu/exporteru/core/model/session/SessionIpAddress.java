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
public final class SessionIpAddress implements Serializable {

    @Column(name = "ip_address", nullable = false, updatable = false)
    private String value;

    private SessionIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.session.ip.empty");
        }

        this.value = ipAddress;
    }

    public static SessionIpAddress of(String ipAddress) {
        return new SessionIpAddress(ipAddress);
    }

    @Override
    public String toString() {
        return value;
    }
}
