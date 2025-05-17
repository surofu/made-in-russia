package com.surofu.madeinrussia.core.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserRegistrationDate implements Serializable {
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime registrationDate;

    private UserRegistrationDate(ZonedDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public static UserRegistrationDate of(ZonedDateTime registrationDate) {
        return new UserRegistrationDate(registrationDate);
    }

    @Override
    public String toString() {
        return registrationDate.toString();
    }
}
