package com.surofu.madeinrussia.infrastructure.persistence.product.packageOption;

import com.surofu.madeinrussia.core.model.product.packageOption.ProductPackageOption;
import com.surofu.madeinrussia.core.repository.ProductPackageOptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductPackageOptionRepository implements ProductPackageOptionsRepository {

    private final SpringDataProductPackageOptionRepository repository;

    @Override
    public List<ProductPackageOptionView> getAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsByProductIdAndLang(productId, lang);
    }

    public List<ProductPackageOptionWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsWithTranslationsByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductPackageOption> getAllByProductId(Long id) {
        return repository.findAllByProductId(id);
    }

    @Override
    public void deleteAll(Collection<ProductPackageOption> oldProductPackageOptions) {
        repository.deleteAll(oldProductPackageOptions);
    }

    @Override
    public void saveAll(Collection<ProductPackageOption> productPackageOptionSet) {
        repository.saveAll(productPackageOptionSet);
    }
}
