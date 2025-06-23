package com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails;

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
public final class ProductDeliveryMethodDetailsLastModificationDate implements Serializable {

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private ProductDeliveryMethodDetailsLastModificationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата последнего изменения способа доставки товара не может быть пустой");
        }

        this.value = date;
    }

    public static ProductDeliveryMethodDetailsLastModificationDate of(ZonedDateTime date) {
        return new ProductDeliveryMethodDetailsLastModificationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
