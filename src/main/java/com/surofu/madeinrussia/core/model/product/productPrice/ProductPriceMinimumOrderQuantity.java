package com.surofu.madeinrussia.core.model.product.productPrice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceMinimumOrderQuantity implements Serializable {

    @Column(name = "minimum_order_quantity", nullable = false)
    private Integer value;

    private ProductPriceMinimumOrderQuantity(Integer minimumOrderQuantity) {
        if (minimumOrderQuantity == null) {
            throw new IllegalArgumentException("The price discount cannot be null.");
        }

        if (minimumOrderQuantity < 0) {
            throw new IllegalArgumentException("The price discount cannot be negative.");
        }

        this.value = minimumOrderQuantity;
    }

    public static ProductPriceMinimumOrderQuantity of(Integer minimumOrderQuantity) {
        return new ProductPriceMinimumOrderQuantity(minimumOrderQuantity);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
