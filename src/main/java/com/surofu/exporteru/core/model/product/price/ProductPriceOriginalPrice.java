package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceOriginalPrice implements Serializable {

    @Column(name = "original_price", nullable = false, columnDefinition = "decimal(15, 2)")
    private BigDecimal value;

    private ProductPriceOriginalPrice(BigDecimal originalPrice) {
        if (originalPrice == null) {
            throw new LocalizedValidationException("validation.product.price.original.empty");
        }

        if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new LocalizedValidationException("validation.product.price.original.negative");
        }

        if (integerDigits(originalPrice) > 15) {
            // Todo: Correct message
            throw new LocalizedValidationException("validation.product.price.original.negative");
        }

        this.value = originalPrice;
    }

    public static ProductPriceOriginalPrice of(BigDecimal originalPrice) {
        return new ProductPriceOriginalPrice(originalPrice);
    }

    private int integerDigits(BigDecimal n) {
        return n.signum() == 0 ? 1 : n.precision() - n.scale();
    }


    @Override
    public String toString() {
        return value.toString();
    }
}
