package com.surofu.madeinrussia.core.model.deliveryMethod;

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

    @Column(name = "name", unique = true, nullable = false)
    private String value;

    private DeliveryMethodName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название способа доставки не может быть пустым");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Название способа доставки не может быть больше 255 символов");
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
