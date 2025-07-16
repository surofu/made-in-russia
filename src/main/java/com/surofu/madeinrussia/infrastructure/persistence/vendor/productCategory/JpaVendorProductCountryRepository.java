package com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory;

import com.surofu.madeinrussia.core.repository.VendorProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorProductCountryRepository implements VendorProductCategoryRepository {

    private final SpringDataVendorProductCountryRepository repository;

    @Override
    public List<VendorProductCategoryView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang) {
        return repository.findAllViewsByVendorDetailsIdAndLang(id, lang);
    }
}
