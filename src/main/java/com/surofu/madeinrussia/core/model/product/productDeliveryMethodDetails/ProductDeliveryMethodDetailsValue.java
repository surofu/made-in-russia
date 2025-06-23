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
public final class ProductDeliveryMethodDetailsValue implements Serializable {

    @Column(name = "value", nullable = false)
    private String value;

    private ProductDeliveryMethodDetailsValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Срок способа доставки товара не может быть пустым");
        }

        this.value = value;
    }

    public static ProductDeliveryMethodDetailsValue of(String value) {
        return new ProductDeliveryMethodDetailsValue(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
