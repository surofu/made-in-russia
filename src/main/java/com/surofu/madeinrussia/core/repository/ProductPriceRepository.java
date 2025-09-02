package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.price.ProductPrice;
import com.surofu.madeinrussia.infrastructure.persistence.product.price.ProductPriceView;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface ProductPriceRepository {
    List<ProductPriceView> findAllViewsByProductId(Long productId, Locale locale);

    List<ProductPrice> getAllByProductId(Long vendorId);

    void deleteAll(Collection<ProductPrice> oldProductPrices);

    void saveAll(Collection<ProductPrice> productPriceList);
}
