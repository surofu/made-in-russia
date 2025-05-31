package com.surofu.madeinrussia.core.model.product.productPrice;

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
public final class ProductPriceLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false)
    private ZonedDateTime value;

    private ProductPriceLastModificationDate(ZonedDateTime date) {
        this.value = date;
    }

    public static ProductPriceLastModificationDate of(ZonedDateTime date) {
        return new ProductPriceLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
