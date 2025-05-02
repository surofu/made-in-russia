package com.surofu.madeinrussia.core.model.product;

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
public final class ProductDiscount implements Serializable {
    @Column(name = "discount")
    private BigDecimal value;

    private ProductDiscount(BigDecimal value) {
        this.value = value;
    }

    public static ProductDiscount of(BigDecimal value) {
        return new ProductDiscount(value);
    }

    public static ProductDiscount of(String value) {
        return new ProductDiscount(new BigDecimal(value));
    }
}
