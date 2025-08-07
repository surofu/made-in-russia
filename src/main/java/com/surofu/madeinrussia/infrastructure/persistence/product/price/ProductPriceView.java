package com.surofu.madeinrussia.infrastructure.persistence.product.price;

import com.surofu.madeinrussia.core.model.product.price.*;

public interface ProductPriceView {
    Long getId();

    ProductPriceQuantityRange getQuantityRange();

    ProductPriceCurrency getCurrency();

    ProductPriceUnit getUnit();

    ProductPriceOriginalPrice getOriginalPrice();

    ProductPriceDiscount getDiscount();

    ProductPriceDiscountedPrice getDiscountedPrice();

    ProductPriceCreationDate getCreationDate();

    ProductPriceLastModificationDate getLastModificationDate();
}
