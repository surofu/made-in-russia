package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
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

    public ProductPriceOriginalPrice(BigDecimal originalPrice) {
        if (originalPrice == null) {
            throw new LocalizedValidationException("validation.product.price.original.empty");
        }
        if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new LocalizedValidationException("validation.product.price.original.negative");
        }
        if (integerDigits(originalPrice) > 15) {
            throw new LocalizedValidationException("validation.product.price.original.too_long");
        }
        this.value = originalPrice;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductPriceOriginalPrice that)) {
            return false;
        }
      return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    private int integerDigits(BigDecimal n) {
        return n.signum() == 0 ? 1 : n.precision() - n.scale();
    }
}
