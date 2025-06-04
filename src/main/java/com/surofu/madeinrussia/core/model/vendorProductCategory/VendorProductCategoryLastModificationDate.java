package com.surofu.madeinrussia.core.model.vendorProductCategory;

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
public final class VendorProductCategoryLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private VendorProductCategoryLastModificationDate(ZonedDateTime lastModificationDate) {
        if (lastModificationDate == null) {
            throw new IllegalArgumentException("Дата последнего изменения категории товаров продавца не может быть пустой");
        }

        this.value = lastModificationDate;
    }

    public static VendorProductCategoryLastModificationDate of(ZonedDateTime lastModificationDate) {
        return new VendorProductCategoryLastModificationDate(lastModificationDate);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
