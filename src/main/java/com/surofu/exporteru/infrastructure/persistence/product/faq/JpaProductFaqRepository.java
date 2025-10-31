package com.surofu.exporteru.infrastructure.persistence.product.faq;

import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.repository.ProductFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductFaqRepository implements ProductFaqRepository {

    private final SpringDataProductFaqRepository repository;

    @Override
    public List<ProductFaqView> findAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsByProductIdAndLang(productId, lang);
    }

    public List<ProductFaqWithTranslationsView> findAllWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsWithTranslationsByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductFaq> getAllByProductId(Long id) {
        return repository.findAllByProductId(id);
    }

    @Override
    public void deleteAll(Collection<ProductFaq> oldProductFaq) {
        repository.deleteAll(oldProductFaq);
    }

    @Override
    public void saveAll(Collection<ProductFaq> productFaqSet) {
        repository.saveAll(productFaqSet);
    }
}
