package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsView;

import java.util.List;

public interface ProductDeliveryMethodDetailsRepository {
    List<ProductDeliveryMethodDetailsView> getAllViewsByProductIdAndLang(Long productId, String lang);
}
