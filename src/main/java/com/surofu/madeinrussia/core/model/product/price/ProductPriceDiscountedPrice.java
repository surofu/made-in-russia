package com.surofu.madeinrussia.core.model.product.price;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceDiscountedPrice implements Serializable {

    @Formula("original_price * (1 - discount / 100)")
    private BigDecimal value;

    private ProductPriceDiscountedPrice(BigDecimal value) {
        this.value = value;
    }

    public static ProductPriceDiscountedPrice of(BigDecimal price) {
        return new ProductPriceDiscountedPrice(price);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
