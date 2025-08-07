package com.surofu.madeinrussia.core.model.product.price;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceUnit implements Serializable {

    @Column(name = "quantity_unit", nullable = false)
    private String value;

    private ProductPriceUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Единицы измерения цены товара не могут быть пустыми");
        }

        if (unit.length() > 255) {
            throw new IllegalArgumentException("Единицы измерения цены товара не могут быть больше 255 символов");
        }

        this.value = unit;
    }

    public static ProductPriceUnit of(String unit) {
        return new ProductPriceUnit(unit);
    }

    @Override
    public String toString() {
        return value;
    }
}
