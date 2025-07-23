package com.surofu.madeinrussia.core.model.advertisement;

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
public final class AdvertisementLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private AdvertisementLastModificationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Last modification date cannot be null or empty.");
        }

        if (date.isAfter(ZonedDateTime.now())) {
            throw new IllegalArgumentException("Last modification date cannot be after now.");
        }

        this.value = date;
    }

    public static AdvertisementLastModificationDate of(ZonedDateTime title) {
        return new AdvertisementLastModificationDate(title);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
