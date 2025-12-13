package com.surofu.exporteru.infrastructure.persistence.vendor;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.repository.VendorDetailsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaVendorDetailsRepository implements VendorDetailsRepository {

    private final SpringDataVendorDetailsRepository repository;

    @Override
    public Optional<VendorDetails> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<VendorDetails> getByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    @Override
    public Long getViewsCountById(Long id) {
        return repository.findViewsCountById(id);
    }

    @Override
    public boolean existsByInn(VendorDetailsInn inn) {
        return repository.existsByInn_Value(inn.getValue());
    }

    @Override
    public boolean existsByInnAndNotVendorDetailsId(VendorDetailsInn inn, Long vendorId) {
        return repository.existsByInn_ValueAndIdNot(inn.getValue(), vendorId);
    }

    @Override
    public VendorDetails save(VendorDetails vendorDetails) {
        return repository.save(vendorDetails);
    }
}
