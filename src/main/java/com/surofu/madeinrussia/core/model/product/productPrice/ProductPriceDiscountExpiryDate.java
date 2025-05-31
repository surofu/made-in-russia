package com.surofu.madeinrussia.core.model.product.productPrice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceDiscountExpiryDate implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_date", nullable = false)
    private ZonedDateTime value;

    private ProductPriceDiscountExpiryDate(ZonedDateTime date) {
        this.value = date;
    }

    public static ProductPriceDiscountExpiryDate of(ZonedDateTime date) {
        return new ProductPriceDiscountExpiryDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
