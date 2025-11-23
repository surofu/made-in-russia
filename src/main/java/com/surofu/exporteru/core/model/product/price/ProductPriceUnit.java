package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
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
public final class ProductPriceUnit implements Serializable {

    @Column(name = "quantity_unit", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "unit_translations", nullable = false, columnDefinition = "hstore")
    private Map<String, String> translations = new HashMap<>();

    private ProductPriceUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.product.price.unit.empty");
        }

        if (unit.length() > 255) {
            throw new LocalizedValidationException("validation.product.price.unit.max_length");
        }

        this.value = unit;
    }

    public static ProductPriceUnit of(String unit) {
        return new ProductPriceUnit(unit);
    }

    @Override
    public String toString() {
        return value;
    }
}
