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

        this.from = from;
        this.to = to;
    }

    public static ProductPriceQuantityRange of(Integer from, Integer to) {
        return new ProductPriceQuantityRange(from, to);
    }

    public static ProductPriceQuantityRange of(String from, String to) {
        if (from == null) {
            throw new IllegalArgumentException("Цена товара не может быть пустой");
        }

        if (from.contains("-")) {
            String[] split = from.split("-");

            if (split.length != 2) {
                throw new IllegalArgumentException(String.format("Неверный формат цены товара: '%s'", from));
            }

            String fromString = split[0].trim();
            String toString = split[1].trim();

            int fromInt;
            int toInt;

            try {
                fromInt = Integer.parseInt(fromString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Неверный формат начальной цены товара: '%s'", from));
            }

            try {
                toInt = Integer.parseInt(toString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Неверный формат конечной цены товара: '%s'", from));
            }

            return new ProductPriceQuantityRange(fromInt, toInt);
        }

        int fromInt;
        int toInt;

        try {
            fromInt = Integer.parseInt(from);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Неверный формат цены товара: '%s'", from));
        }

        if (to != null && !to.trim().isEmpty()) {
            try {
                toInt = Integer.parseInt(to);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Неверный формат цены товара: '%s'", from));
            }

            return new ProductPriceQuantityRange(fromInt, toInt);

        }

        return new ProductPriceQuantityRange(fromInt, fromInt);
    }

    @Override
    public String toString() {
        return String.format("ProductPriceQuantityRange{from=%s, to=%s}", from, to);
    }
}
