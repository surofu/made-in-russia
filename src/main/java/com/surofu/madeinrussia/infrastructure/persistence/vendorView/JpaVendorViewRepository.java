package com.surofu.madeinrussia.infrastructure.persistence.vendorView;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorView.VendorView;
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
    public void saveVendorView(VendorView vendorView) {
        repository.save(vendorView);
    }

    @Override
    public boolean notExists(VendorView vendorView) {
        return repository.notExistsByVendorDetailsIdAndUserId(vendorView.getVendorDetails().getId(), vendorView.getUser().getId());
    }
}
