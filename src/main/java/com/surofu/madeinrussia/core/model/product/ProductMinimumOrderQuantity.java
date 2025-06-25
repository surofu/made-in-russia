package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductMinimumOrderQuantity implements Serializable {

    @Column(name = "minimum_order_quantity", nullable = false, columnDefinition = "int")
    private Integer value;

    private ProductMinimumOrderQuantity(Integer minimumOrderQuantity) {
        if (minimumOrderQuantity == null) {
            throw new IllegalArgumentException("Минимальное количество товара не может быть пустым");
        }

        if (minimumOrderQuantity < 0) {
            throw new IllegalArgumentException("Минимальное количество товара не может быть отрицательным");
        }

        this.value = minimumOrderQuantity;
    }

    public static ProductMinimumOrderQuantity of(Integer minimumOrderQuantity) {
        return new ProductMinimumOrderQuantity(minimumOrderQuantity);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
