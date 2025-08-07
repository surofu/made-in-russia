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
public final class ProductPriceCurrency implements Serializable {

    @Column(name = "currency", nullable = false)
    private String value;

    private ProductPriceCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Валюта цены товара не может быть пустой");
        }

        if (currency.length() > 255) {
            throw new IllegalArgumentException("Валюта цены товара не может быть больше 255 символов");
        }

        this.value = currency;
    }

    public static ProductPriceCurrency of(String currency) {
        return new ProductPriceCurrency(currency);
    }

    @Override
    public String toString() {
        return value;
    }
}
