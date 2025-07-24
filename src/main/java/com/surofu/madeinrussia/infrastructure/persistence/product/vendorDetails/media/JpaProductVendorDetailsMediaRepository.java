package com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.media;

import com.surofu.madeinrussia.core.repository.ProductVendorDetailsMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductVendorDetailsMediaRepository implements ProductVendorDetailsMediaRepository {

    private final SpringDataProductVendorDetailsMediaRepository repository;

    @Override
    public List<ProductVendorDetailsMediaView> getAllViewsByProductVendorDetailsIdAndLang(Long id, String lang) {
        return repository.findAllViewsByProductVendorDetailsIdAndLang(id, lang);
    }

    @Override
    public List<ProductVendorDetailsMediaWithTranslationsView> getAllViewsWithTranslationsByProductVendorDetailsId(Long id, String lang) {
        return repository.findAllViewsWithTranslationsByProductVendorDetailsIdAndLang(id, lang);
    }
}
