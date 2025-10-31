package com.surofu.exporteru.infrastructure.persistence.product.deliveryMethodDetails;

import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.repository.ProductDeliveryMethodDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductDeliveryMethodDetailsRepository implements ProductDeliveryMethodDetailsRepository {

    private final SpringDataProductDeliveryMethodDetailsRepository repository;

    @Override
    public List<ProductDeliveryMethodDetailsView> getAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductDeliveryMethodDetailsWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsWithTranslationsByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductDeliveryMethodDetails> getAllByProductId(Long id) {
        return repository.findAllByProductId(id);
    }

    @Override
    public void deleteAll(Collection<ProductDeliveryMethodDetails> oldProductDeliveryMethodDetails) {
        repository.deleteAll(oldProductDeliveryMethodDetails);
    }

    @Override
    public void saveAll(Collection<ProductDeliveryMethodDetails> productDeliveryMethodDetailsSet) {
        repository.saveAll(productDeliveryMethodDetailsSet);
    }
}
