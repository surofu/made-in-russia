package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.madeinrussia.infrastructure.persistence.product.productDeliveryMethodDetails.ProductDeliveryMethodDetailsView;

import java.util.List;

public interface ProductDeliveryMethodDetailsRepository {
    List<ProductDeliveryMethodDetails> getAllByProductId(Long productId);

    List<ProductDeliveryMethodDetailsView> getAllViewsByProductId(Long productId);
}
