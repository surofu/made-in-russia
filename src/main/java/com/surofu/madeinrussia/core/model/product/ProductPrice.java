package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPrice implements Serializable {
    @Column(name = "price")
    private BigDecimal value;

    private ProductPrice(BigDecimal value) {
        this.value = value;
    }

    public static ProductPrice of(BigDecimal value) {
        return new ProductPrice(value);
    }

    public static ProductPrice of(String value) {
        return new ProductPrice(new BigDecimal(value));
    }

    public BigDecimal makeDiscount(BigDecimal discount) {
        if (value == null || discount == null) {
            return BigDecimal.ZERO;
        }

        return value.multiply(
                BigDecimal.ONE.subtract(
                        discount.divide(BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        )
                )
        ).setScale(2, RoundingMode.HALF_UP);
    }
}
