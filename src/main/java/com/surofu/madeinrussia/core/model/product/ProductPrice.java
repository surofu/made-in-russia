package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPrice implements Serializable {

    @Column(name = "price", nullable = false)
    private BigDecimal originalPrice;

    @Column(columnDefinition = "decimal(5, 2) default 0")
    private BigDecimal discount;

    @Formula("originalPrice * (1 - discount / 100)")
    private BigDecimal discountedPrice;

    @Column(name = "price_unit", nullable = false, columnDefinition = "default ''")
    private String unit;

    private ProductPrice(BigDecimal originalPrice, BigDecimal discount, String unit) {
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.unit = unit;
    }

    public static ProductPrice of(BigDecimal originalPrice, BigDecimal discount, String unit) {
        return new ProductPrice(originalPrice, discount, unit);
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(0);
        decimalFormat.setGroupingUsed(false);

        return decimalFormat.format(discountedPrice);
    }
}
