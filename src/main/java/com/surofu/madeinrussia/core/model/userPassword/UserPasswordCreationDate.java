package com.surofu.madeinrussia.core.model.userPassword;

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
public final class UserPasswordCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private UserPasswordCreationDate(ZonedDateTime date) {
        this.value = date;
    }

    public static UserPasswordCreationDate of(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата создания пароля пользователя не может быть пустой");
        }

        return new UserPasswordCreationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}