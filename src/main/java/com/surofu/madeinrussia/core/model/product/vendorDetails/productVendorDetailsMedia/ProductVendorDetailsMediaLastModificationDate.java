package com.surofu.madeinrussia.core.model.product.vendorDetails.productVendorDetailsMedia;

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
public final class ProductVendorDetailsMediaLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private ProductVendorDetailsMediaLastModificationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата последнего изменения медиа о продавце в товаре не может быть пустой");
        }

        this.value = date;
    }

    public static ProductVendorDetailsMediaLastModificationDate of(ZonedDateTime date) {
        return new ProductVendorDetailsMediaLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
