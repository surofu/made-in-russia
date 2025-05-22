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

    @Column(nullable = false)
    private String name;

    private ProductCharacteristicName(String name) {
        this.name = name;
    }

    public static ProductCharacteristicName of(String name) {
        return new ProductCharacteristicName(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
