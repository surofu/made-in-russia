package com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails;

import com.surofu.madeinrussia.core.repository.ProductDeliveryMethodDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductDeliveryMethodDetailsRepository implements ProductDeliveryMethodDetailsRepository {

    private final SpringDataProductDeliveryMethodDetailsRepository repository;

    @Override
    public List<ProductDeliveryMethodDetailsView> getAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsByProductIdAndLang(productId, lang);
    }

    public List<ProductDeliveryMethodDetailsWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsWithTranslationsByProductIdAndLang(productId, lang);
    }
}
