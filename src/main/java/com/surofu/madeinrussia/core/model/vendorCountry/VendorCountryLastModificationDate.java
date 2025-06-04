package com.surofu.madeinrussia.core.model.vendorCountry;

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
public final class VendorCountryLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private VendorCountryLastModificationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата последнего изменения страны продавца не может быть пустой");
        }

        this.value = date;
    }

    public static VendorCountryLastModificationDate of(ZonedDateTime date) {
        return new VendorCountryLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
