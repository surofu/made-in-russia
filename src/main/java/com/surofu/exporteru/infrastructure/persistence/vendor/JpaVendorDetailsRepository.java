package com.surofu.exporteru.infrastructure.persistence.vendor;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.repository.VendorDetailsRepository;
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

    @Override
    public boolean existsByInnAndNotVendorDetailsId(VendorDetailsInn inn, Long vendorId) {
        return repository.existsByInnAndIdNot(inn, vendorId);
    }

    @Override
    public VendorDetails save(VendorDetails vendorDetails) {
        return repository.save(vendorDetails);
    }
}
