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

    @Column(nullable = false)
    private BigDecimal price;

    @Column(columnDefinition = "decimal(5, 2) default 0")
    private BigDecimal discount;

    @Formula("price * (1 - discount / 100)")
    private BigDecimal discountedPrice;

    private ProductPrice(BigDecimal price, BigDecimal discount) {
        this.price = price;
        this.discount = discount;
    }

    public static ProductPrice of(BigDecimal price, BigDecimal discount) {
        return new ProductPrice(price, discount);
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(0);
        decimalFormat.setGroupingUsed(false);

        return decimalFormat.format(price);
    }
}
