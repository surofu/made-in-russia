package com.surofu.madeinrussia.infrastructure.persistence.product.media;

import com.surofu.madeinrussia.core.repository.ProductMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductMediaRepository implements ProductMediaRepository {

    private final SpringDataProductMediaRepository repository;

    @Override
    public List<ProductMediaView> getAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductMediaWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllWithTranslationsByProductIdAndLang(productId, lang);
    }
}
