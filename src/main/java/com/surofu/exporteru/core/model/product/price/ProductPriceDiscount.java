package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceDiscount implements Serializable {

    @Column(name = "discount", nullable = false, columnDefinition = "decimal(5, 2)")
    private BigDecimal value;

    private ProductPriceDiscount(BigDecimal priceDiscount) {
        if (priceDiscount == null) {
            throw new LocalizedValidationException("validation.product.price.discount.empty");
        }

        if (priceDiscount.compareTo(BigDecimal.ZERO) < 0) {
            throw new LocalizedValidationException("validation.product.price.discount.negative");
        }

        if (priceDiscount.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new LocalizedValidationException("validation.product.price.discount.max");
        }

        this.value = priceDiscount;
    }

    public static ProductPriceDiscount of(BigDecimal priceDiscount) {
        return new ProductPriceDiscount(priceDiscount);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
