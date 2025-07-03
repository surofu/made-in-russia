package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import com.surofu.madeinrussia.core.repository.VendorFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaVendorFaqRepository implements VendorFaqRepository {

    private final SpringDataVendorFaqRepository repository;

    @Override
    public void save(VendorFaq vendorFaq) {
        repository.save(vendorFaq);
    }
}
