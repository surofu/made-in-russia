package com.surofu.madeinrussia.infrastructure.persistence.product.characteristic;

import com.surofu.madeinrussia.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.repository.ProductCharacteristicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductCharacteristicRepository implements ProductCharacteristicRepository {

    private final SpringDataProductCharacteristicRepository repository;

    @Override
    public List<ProductCharacteristicView> findAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllByProductIdAndLang(productId, lang);
    }

    public List<ProductCharacteristicWithTranslationsView> findAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllWithTranslationsByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductCharacteristic> getAllByProductId(Long id) {
        return repository.getAllByProductId(id);
    }

    @Override
    public void deleteAll(Collection<ProductCharacteristic> oldProductCharacteristics) {
        repository.deleteAll(oldProductCharacteristics);
    }

    @Override
    public void saveAll(Collection<ProductCharacteristic> productCharacteristicSet) {
        repository.saveAll(productCharacteristicSet);
    }
}
