package com.surofu.madeinrussia.core.model.advertisement;

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
public final class AdvertisementCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private AdvertisementCreationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Creation date cannot be null or empty.");
        }

        if (date.isAfter(ZonedDateTime.now())) {
            throw new IllegalArgumentException("Creation date cannot be after now.");
        }

        this.value = date;
    }

    public static AdvertisementCreationDate of(ZonedDateTime title) {
        return new AdvertisementCreationDate(title);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
