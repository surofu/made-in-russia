package com.surofu.madeinrussia.infrastructure.persistence.vendorView;

import com.surofu.madeinrussia.core.model.vendorDetails.view.VendorView;
import com.surofu.madeinrussia.core.repository.VendorViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaVendorViewRepository implements VendorViewRepository {

    private final SpringDataVendorViewRepository repository;

    @Override
    public Long getCountByVendorDetailsId(Long vendorDetailsId) {
        return repository.countByVendorDetailsId(vendorDetailsId);
    }

    @Override
    public void save(VendorView vendorView) {
        repository.save(vendorView);
    }

    @Override
    public boolean notExists(VendorView vendorView) {
        return repository.existsByVendorDetailsAndUser(vendorView.getVendorDetails(), vendorView.getUser());
    }
}
