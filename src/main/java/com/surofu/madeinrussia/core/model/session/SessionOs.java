package com.surofu.madeinrussia.core.model.session;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SessionOs implements Serializable {
    private String os;

    private SessionOs(String os) {
        this.os = os;
    }

    public static SessionOs of(String os) {
        return new SessionOs(os);
    }
}
