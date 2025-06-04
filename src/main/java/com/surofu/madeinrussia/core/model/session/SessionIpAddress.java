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
public final class SessionIpAddress implements Serializable {

    @Column(name = "ip_address", nullable = false, updatable = false)
    private String value;

    private SessionIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("IP адрес сессии не может быть пустым");
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
