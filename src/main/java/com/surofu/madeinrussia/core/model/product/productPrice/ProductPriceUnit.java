package com.surofu.madeinrussia.core.model.product.productPrice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceUnit implements Serializable {

    @Column(name = "unit", nullable = false)
    private String value;

    private ProductPriceUnit(String priceUnit) {
        this.value = priceUnit;
    }

    public static ProductPriceUnit of(String priceUnit) {
        return new ProductPriceUnit(priceUnit);
    }

    @Override
    public String toString() {
        return value;
    }
}
