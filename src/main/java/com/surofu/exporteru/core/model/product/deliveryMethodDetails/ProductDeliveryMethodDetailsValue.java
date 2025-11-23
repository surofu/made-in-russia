package com.surofu.exporteru.core.model.product.deliveryMethodDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductDeliveryMethodDetailsValue implements Serializable {

    @Column(name = "value", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value_translations")
    private Map<String, String> translations = new HashMap<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDeliveryMethodDetailsValue productDeliveryMethodDetailsValue)) return false;
        return Objects.equals(value, productDeliveryMethodDetailsValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
