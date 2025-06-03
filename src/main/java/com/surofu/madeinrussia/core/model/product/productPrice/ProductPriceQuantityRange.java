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
public final class ProductPriceQuantityRange implements Serializable {

    @Column(name = "quantity_from", nullable = false, columnDefinition = "int")
    private Integer from;

    @Column(name = "quantity_to", nullable = false, columnDefinition = "int")
    private Integer to;

    private ProductPriceQuantityRange(Integer from, Integer to) {
        if (from == null) {
            throw new IllegalArgumentException("Начальное количество цены товара не может быть пустым");
        }

        if (to == null) {
            throw new IllegalArgumentException("Конечное количество цены товара не может быть пустым");
        }

        if (from < 0) {
            throw new IllegalArgumentException("Начальное количество цены товара не может быть отрицательным");
        }

        if (to < 0) {
            throw new IllegalArgumentException("Конечное количество цены товара не может быть отрицательным");
        }

        if (from > to) {
            throw new IllegalArgumentException("Начальное количество цены товара не может быть больше конечного");
        }

        this.from = from;
        this.to = to;
    }

    public static ProductPriceQuantityRange of(Integer from, Integer to) {
        return new ProductPriceQuantityRange(from, to);
    }

    @Override
    public String toString() {
        return String.format("ProductPriceQuantityRange{from=%s, to=%s}", from, to);
    }
}
