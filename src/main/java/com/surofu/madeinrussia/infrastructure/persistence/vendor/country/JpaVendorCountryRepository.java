package com.surofu.madeinrussia.infrastructure.persistence.vendor.country;

import com.surofu.madeinrussia.core.repository.VendorCountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorCountryRepository implements VendorCountryRepository {

    private final SpringDataVendorCountryRepository repository;

    @Override
    public List<VendorCountryView> getAllViewsByVendorDetailsId(Long id) {
        return repository.findAllViewsByVendorDetails_Id(id);
    }
}
