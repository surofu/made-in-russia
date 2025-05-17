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
    @Column(nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime lastModificationDate;

    private UserPasswordLastModificationDate(ZonedDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public static UserPasswordLastModificationDate of(ZonedDateTime lastModificationDate) {
        return new UserPasswordLastModificationDate(lastModificationDate);
    }

    @Override
    public String toString() {
        return lastModificationDate.toString();
    }
}