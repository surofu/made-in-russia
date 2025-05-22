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

    @Column(nullable = false)
    private String value;

    private ProductCharacteristicValue(String value) {
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
