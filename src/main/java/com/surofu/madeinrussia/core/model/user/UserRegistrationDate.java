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
    @Column(name = "registration_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private UserRegistrationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата регистрации пользователя не может быть пустой");
        }

        this.value = date;
    }

    public static UserRegistrationDate of(ZonedDateTime date) {
        return new UserRegistrationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
