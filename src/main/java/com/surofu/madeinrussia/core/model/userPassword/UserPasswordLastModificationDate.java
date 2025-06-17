package com.surofu.madeinrussia.core.model.userPassword;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserPasswordLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private UserPasswordLastModificationDate(ZonedDateTime date) {
        this.value = date;
    }

    public static UserPasswordLastModificationDate of(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата последнего изменения пароля пользователя не может быть пустой");
        }

        return new UserPasswordLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}