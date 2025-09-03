package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsWithTranslationsView;

import java.util.Collection;
import java.util.List;

public interface ProductDeliveryMethodDetailsRepository {
    List<ProductDeliveryMethodDetailsView> getAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductDeliveryMethodDetailsWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang);

    List<ProductDeliveryMethodDetails> getAllByProductId(Long id);

    void deleteAll(Collection<ProductDeliveryMethodDetails> oldProductDeliveryMethodDetails);

    void saveAll(Collection<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet);
}
