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

    @Column(nullable = false)
    private String ipAddress;

    private SessionIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public static SessionIpAddress of(String ipAddress) {
        return new SessionIpAddress(ipAddress);
    }

    @Override
    public String toString() {
        return ipAddress;
    }
}
