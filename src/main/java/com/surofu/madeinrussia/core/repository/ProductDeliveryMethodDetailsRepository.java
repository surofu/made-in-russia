package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetails;

import java.util.List;

public interface ProductDeliveryMethodDetailsRepository {
    List<ProductDeliveryMethodDetails> findAllByProductId(Long productId);
}
