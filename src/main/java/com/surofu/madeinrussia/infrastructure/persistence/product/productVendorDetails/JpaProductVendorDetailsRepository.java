package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails;

import com.surofu.madeinrussia.core.repository.ProductVendorDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductVendorDetailsRepository implements ProductVendorDetailsRepository {

    private final SpringDataProductVendorDetailsRepository repository;

    @Override
    public Optional<ProductVendorDetailsView> getViewByProductIdAndLang(Long productId, String lang) {
        return repository.findViewByProductIdAndLang(productId, lang);
    }

    @Override
    public Optional<ProductVendorDetailsWithTranslationsView> getViewWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findViewWithTranslationsByProductIdAndLang(productId, lang);
    }
}
