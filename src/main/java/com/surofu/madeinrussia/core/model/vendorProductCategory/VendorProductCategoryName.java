package com.surofu.madeinrussia.core.model.vendorProductCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorProductCategoryName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    private VendorProductCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории товаров продавца не может быть пустым");
        }

        this.value = name;
    }

    public static VendorProductCategoryName of(String name) {
        return new VendorProductCategoryName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
