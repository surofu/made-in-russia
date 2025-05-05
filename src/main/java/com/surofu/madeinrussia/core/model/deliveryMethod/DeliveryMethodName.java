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
    @Column(name = "name")
    private String value;

    private DeliveryMethodName(String value) {
        this.value = value;
    }

    public static DeliveryMethodName of(String value) {
        return new DeliveryMethodName(value);
    }
}
