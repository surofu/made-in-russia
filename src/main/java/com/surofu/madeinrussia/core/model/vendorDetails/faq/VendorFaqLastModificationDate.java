package com.surofu.madeinrussia.core.model.vendorDetails.faq;

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
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorFaqLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private VendorFaqLastModificationDate(ZonedDateTime date) {
        this.value = Objects.requireNonNullElseGet(date, ZonedDateTime::now);
    }

    public static VendorFaqLastModificationDate of(ZonedDateTime date) {
        return new VendorFaqLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
