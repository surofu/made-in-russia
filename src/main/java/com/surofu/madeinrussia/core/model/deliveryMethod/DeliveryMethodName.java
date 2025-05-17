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

    @Column(unique = true, nullable = false)
    private String name;

    private DeliveryMethodName(String name) {
        this.name = name;
    }

    public static DeliveryMethodName of(String name) {
        return new DeliveryMethodName(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
