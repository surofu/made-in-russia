package com.surofu.exporteru.core.model.product.packageOption;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPackageOptionPrice implements Serializable {

    @Column(name = "price", nullable = false, columnDefinition = "decimal(15, 2)")
    private BigDecimal value;

    private ProductPackageOptionPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Цена варианта упаковки товара не может быть пустой");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цена варианта упаковки товара не может быть отрицательной");
        }

        this.value = price;
    }

    public static ProductPackageOptionPrice of(BigDecimal price) {
        return new ProductPackageOptionPrice(price);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPackageOptionPrice productPackageOptionPrice)) return false;
        return Objects.equals(value, productPackageOptionPrice.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
