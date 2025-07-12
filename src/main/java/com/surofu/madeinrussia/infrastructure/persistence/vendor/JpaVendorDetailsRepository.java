package com.surofu.madeinrussia.infrastructure.persistence.vendor;

import com.surofu.madeinrussia.core.repository.VendorDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaVendorDetailsRepository implements VendorDetailsRepository {

    private final SpringDataVendorDetailsRepository repository;

    @Override
    public Optional<VendorDetailsView> getViewById(Long id) {
        return repository.findViewById(id);
    }

    @Override
    public Long getViewsCountById(Long id) {
        return repository.findViewsCountById(id);
    }
}
