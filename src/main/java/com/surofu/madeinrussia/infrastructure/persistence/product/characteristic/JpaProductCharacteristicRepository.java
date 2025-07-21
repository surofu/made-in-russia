package com.surofu.madeinrussia.infrastructure.persistence.product.characteristic;

import com.surofu.madeinrussia.core.repository.ProductCharacteristicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductCharacteristicRepository implements ProductCharacteristicRepository {

    private final SpringDataProductCharacteristicRepository repository;

    @Override
    public List<ProductCharacteristicView> findAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductCharacteristicWithTranslationsView> findAllViewsWithTranslationsByProductId(Long productId) {
        return repository.findAllWithTranslationsByProductId(productId);
    }
}
