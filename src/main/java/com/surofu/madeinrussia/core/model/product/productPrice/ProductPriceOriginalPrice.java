package com.surofu.madeinrussia.core.model.product.productPrice;

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

    @Column(name = "original_price", nullable = false, columnDefinition = "decimal(10, 2)")
    private BigDecimal value;

    private ProductPriceOriginalPrice(BigDecimal originalPrice) {
        if (originalPrice == null) {
            throw new IllegalArgumentException("Оригинальная цена товара не может быть пустой");
        }

        if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Оригинальная цена товара не может быть отрицательной");
        }

        this.value = originalPrice;
    }

    public static ProductPriceOriginalPrice of(BigDecimal originalPrice) {
        return new ProductPriceOriginalPrice(originalPrice);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
