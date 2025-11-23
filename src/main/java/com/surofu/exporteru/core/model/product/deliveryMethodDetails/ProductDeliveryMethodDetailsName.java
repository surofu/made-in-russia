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
public final class ProductDeliveryMethodDetailsName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name_translations")
    private Map<String, String> translations = new HashMap<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDeliveryMethodDetailsName productDeliveryMethodDetailsName)) return false;
        return Objects.equals(value, productDeliveryMethodDetailsName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
