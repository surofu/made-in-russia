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
public final class ProductPriceQuantityRange implements Serializable {

    @Column(name = "from", nullable = false)
    private Integer from;

    @Column(name = "to", nullable = false)
    private Integer to;

    private ProductPriceQuantityRange(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }

    public static ProductPriceQuantityRange of(Integer from, Integer to) {
        return new ProductPriceQuantityRange(from, to);
    }

    @Override
    public String toString() {
        return String.format("ProductPriceQuantityRange{from=%s, to=%s}", from, to);
    }
}
