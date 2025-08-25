package com.surofu.madeinrussia.core.model.vendorDetails.country;

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
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorCountryCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private VendorCountryCreationDate(ZonedDateTime date) {
        this.value = Objects.requireNonNullElseGet(date, ZonedDateTime::now);
    }

    public static VendorCountryCreationDate of(ZonedDateTime date) {
        return new VendorCountryCreationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
