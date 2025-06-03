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
public final class ProductCharacteristicName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    private ProductCharacteristicName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название характеристики не может быть пустой");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Название характеристики не может быть больше 255 символов");
        }

        this.value = name;
    }

    public static ProductCharacteristicName of(String name) {
        return new ProductCharacteristicName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
