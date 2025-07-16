package com.surofu.madeinrussia.infrastructure.persistence.vendor;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.repository.VendorDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaVendorDetailsRepository implements VendorDetailsRepository {

    private final SpringDataVendorDetailsRepository repository;

    @Override
    public Long getViewsCountById(Long id) {
        return repository.findViewsCountById(id);
    }

    @Override
    public boolean existsByInn(VendorDetailsInn inn) {
        return repository.existsByInn(inn);
    }
}
