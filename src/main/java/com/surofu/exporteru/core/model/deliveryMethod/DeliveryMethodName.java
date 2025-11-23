package com.surofu.exporteru.core.model.deliveryMethod;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DeliveryMethodName implements Serializable {

    @Column(name = "name", unique = true, nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name_translations")
    private Map<String, String> translations = new HashMap<>();

    private DeliveryMethodName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.delivery_method.name.empty");
        }

        if (name.length() > 255) {
            throw new LocalizedValidationException("validation.delivery_method.name.max_length");
        }

        this.value = name;
    }

    public static DeliveryMethodName of(String name) {
        return new DeliveryMethodName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
