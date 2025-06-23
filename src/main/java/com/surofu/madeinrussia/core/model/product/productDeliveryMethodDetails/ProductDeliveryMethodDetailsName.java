package com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductDeliveryMethodDetailsName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    private ProductDeliveryMethodDetailsName(String name) {
        this.value = name;
    }

    public static ProductDeliveryMethodDetailsName of(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название способа доставки товара не может быть пустым");
        }

        return new ProductDeliveryMethodDetailsName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
