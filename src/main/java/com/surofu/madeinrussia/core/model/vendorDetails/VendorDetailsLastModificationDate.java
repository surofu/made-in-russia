package com.surofu.madeinrussia.core.model.vendorDetails;

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
public final class VendorDetailsLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private VendorDetailsLastModificationDate(ZonedDateTime lastModificationDate) {
        if (lastModificationDate == null) {
            throw new IllegalArgumentException("Дата последнего изменения информации о продавце не может быть пустой");
        }

        this.value = lastModificationDate;
    }

    public static VendorDetailsLastModificationDate of(ZonedDateTime lastModificationDate) {
        return new VendorDetailsLastModificationDate(lastModificationDate);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
