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
public final class ProductPackageOptionPriceUnit implements Serializable {

    @Column(name = "price_unit", nullable = false)
    private String value;

    private ProductPackageOptionPriceUnit(String priceUnit) {
        if (priceUnit == null || priceUnit.trim().isEmpty()) {
            throw new IllegalArgumentException("Валюта цены в варианте упаковки товара не может быть пустой");
        }

        this.value = priceUnit;
    }

    public static ProductPackageOptionPriceUnit of(String priceUnit) {
        return new ProductPackageOptionPriceUnit(priceUnit);
    }

    @Override
    public String toString() {
        return value;
    }
}
