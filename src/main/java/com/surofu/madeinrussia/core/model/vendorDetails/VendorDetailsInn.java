package com.surofu.madeinrussia.core.model.vendorDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsInn implements Serializable {

    @Column(name = "inn", nullable = false, unique = true)
    private String value;

    private VendorDetailsInn(String inn) {
        if (inn == null || inn.trim().isEmpty()) {
            throw new IllegalArgumentException("ИНН продавца не может быть пустым");
        }

        if (inn.length() < 7) {
            throw new IllegalArgumentException("ИНН продавца не может быть меньше 7 символов");
        }

        if (inn.length() > 255) {
            throw new IllegalArgumentException("ИНН продавца не может быть больше 255 символов");
        }

        this.value = inn;
    }

    public static VendorDetailsInn of(String inn) {
        return new VendorDetailsInn(inn);
    }

    @Override
    public String toString() {
        return value;
    }
}
