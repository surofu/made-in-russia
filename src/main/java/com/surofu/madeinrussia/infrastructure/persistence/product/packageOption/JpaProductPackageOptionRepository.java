package com.surofu.madeinrussia.infrastructure.persistence.product.packageOption;

import com.surofu.madeinrussia.core.repository.ProductPackageOptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductPackageOptionRepository implements ProductPackageOptionsRepository {

    private final SpringDataProductPackageOptionRepository repository;

    @Override
    public List<ProductPackageOptionView> getAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllViewsByProductIdAndLang(productId, lang);
    }
}
