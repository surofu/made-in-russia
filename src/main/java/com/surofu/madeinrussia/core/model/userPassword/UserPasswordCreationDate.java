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
    @Column(nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime creationDate;

    private UserPasswordCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public static UserPasswordCreationDate of(ZonedDateTime creationDate) {
        return new UserPasswordCreationDate(creationDate);
    }

    @Override
    public String toString() {
        return creationDate.toString();
    }
}