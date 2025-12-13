package com.surofu.exporteru.core.model.vendorDetails.email;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Generated
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorEmailCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private VendorEmailCreationDate(ZonedDateTime date) {
        this.value = Objects.requireNonNullElseGet(date, ZonedDateTime::now);
    }

    public static VendorEmailCreationDate of(ZonedDateTime date) {
        return new VendorEmailCreationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
