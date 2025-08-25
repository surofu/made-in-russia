package com.surofu.madeinrussia.infrastructure.persistence.vendor.email;

import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmail;
import com.surofu.madeinrussia.core.repository.VendorEmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorEmailRepository implements VendorEmailRepository {

    private final SpringDataVendorEmailRepository repository;

    @Override
    public List<VendorEmail> getAllByVendorDetailsId(Long id) {
        return repository.getAllByVendorDetails_Id(id);
    }
}
