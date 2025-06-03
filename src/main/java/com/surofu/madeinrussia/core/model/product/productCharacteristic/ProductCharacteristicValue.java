package com.surofu.madeinrussia.core.model.product.productCharacteristic;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductCharacteristicValue implements Serializable {

    @Column(name = "value", nullable = false)
    private String value;

    private ProductCharacteristicValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Значение характеристики не может быть пустой");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("Значение характеристики не может быть больше 255 символов");
        }

        this.value = value;
    }

    public static ProductCharacteristicValue of(String value) {
        return new ProductCharacteristicValue(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
