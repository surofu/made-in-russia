package com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory;

import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.repository.VendorProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorProductCountryRepository implements VendorProductCategoryRepository {

    private final SpringDataVendorProductCountryRepository repository;

    @Override
    public List<VendorProductCategoryView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang) {
        return repository.findAllViewsByVendorDetailsIdAndLang(id, lang);
    }

    @Override
    public List<VendorProductCategory> getAllByVendorDetailsId(Long id) {
        return repository.findAllByVendorDetailsId(id);
    }

    @Override
    public void saveAll(Collection<VendorProductCategory> vendorProductCategories) {
        repository.saveAll(vendorProductCategories);
    }

    @Override
    public void deleteAll(Collection<VendorProductCategory> vendorProductCategories) {
        repository.deleteAll(vendorProductCategories);
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
