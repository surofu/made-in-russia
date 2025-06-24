package com.surofu.madeinrussia.core.model.product;

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
public final class ProductDiscountExpirationDate implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "discount_expiration_date", nullable = false, columnDefinition = "timestamptz")
    private ZonedDateTime value;

    private ProductDiscountExpirationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата окончания скидки не может быть пустой");
        }

        this.value = date;
    }

    public static ProductDiscountExpirationDate of(ZonedDateTime date) {
        return new ProductDiscountExpirationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
