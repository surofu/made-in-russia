package com.surofu.madeinrussia.core.model.session;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SessionLastLoginDate implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime lastLoginDate;

    private SessionLastLoginDate(ZonedDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public static SessionLastLoginDate of(ZonedDateTime lastLoginDate) {
        return new SessionLastLoginDate(lastLoginDate);
    }
}
