package com.surofu.madeinrussia.core.model.vendorCountry;

import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorCountryName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    private VendorCountryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название страны продавца не может быть пустым");
        }

        this.value = name;
    }

    public static VendorCountryName of(String name) {
        return new VendorCountryName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
