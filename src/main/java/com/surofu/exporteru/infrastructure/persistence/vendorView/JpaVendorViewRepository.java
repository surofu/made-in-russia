package com.surofu.exporteru.infrastructure.persistence.vendorView;

import com.surofu.exporteru.core.model.vendorDetails.view.VendorView;
import com.surofu.exporteru.core.repository.VendorViewRepository;
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

    @Override
    public Boolean existsByUserIdAndVendorDetailsId(Long userId, Long vendorDetailsId) {
        return repository.existsByUser_IdAndVendorDetails_Id(userId, vendorDetailsId);
    }
}
