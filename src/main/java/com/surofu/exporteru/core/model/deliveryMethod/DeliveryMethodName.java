package com.surofu.exporteru.core.model.deliveryMethod;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DeliveryMethodName implements Serializable {

    // TODO: DeliveryMethodName Translation. Hstore -> Jsonb
    @Column(name = "name", unique = true, nullable = false)
    private String value;

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
