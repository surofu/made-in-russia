package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.price.ProductPriceView;

import java.util.List;
import java.util.Locale;

public interface ProductPriceRepository {
    List<ProductPriceView> findAllViewsByProductId(Long productId, Locale locale);
}
