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
public final class ProductPriceCurrency implements Serializable {

    @Column(name = "currency", nullable = false)
    private String value;

    private ProductPriceCurrency(String priceCurrency) {
        this.value = priceCurrency;
    }

    public static ProductPriceCurrency of(String priceCurrency) {
        return new ProductPriceCurrency(priceCurrency);
    }

    @Override
    public String toString() {
        return value;
    }
}
