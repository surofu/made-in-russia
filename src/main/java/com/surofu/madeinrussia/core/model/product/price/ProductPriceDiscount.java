package com.surofu.madeinrussia.core.model.product.price;

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
public final class ProductPriceDiscount implements Serializable {

    @Column(name = "discount", nullable = false, columnDefinition = "decimal(5, 2)")
    private BigDecimal value;

    private ProductPriceDiscount(BigDecimal priceDiscount) {
        if (priceDiscount == null) {
            throw new IllegalArgumentException("Скидка товара не может быть пустой");
        }

        if (priceDiscount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Скидка товара не может быть отрицательной");
        }

        if (priceDiscount.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Скидка товара не может быть больше 100");
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
