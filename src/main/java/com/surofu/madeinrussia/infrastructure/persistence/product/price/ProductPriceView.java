package com.surofu.madeinrussia.infrastructure.persistence.product.price;

import com.surofu.madeinrussia.core.model.currency.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;

public interface ProductPriceView {
    Long getId();

    Integer getQuantityFrom();

    Integer getQuantityTo();

    CurrencyCode getCurrency();

    String getUnit();

    BigDecimal getOriginalPrice();

    BigDecimal getDiscount();

    BigDecimal getDiscountedPrice();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
