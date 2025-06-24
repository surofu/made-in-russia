package com.surofu.madeinrussia.core.model.product.productPackageOption;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPackageOptionName implements Serializable {

    @Column(name = "name", nullable = false)
    private String value;

    private ProductPackageOptionName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название варианта упаковки товара не может быть пустым");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Название варианта упаковки товара не может быть больше 255 символов");
        }

        this.value = name;
    }

    public static ProductPackageOptionName of(String name) {
        return new ProductPackageOptionName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
