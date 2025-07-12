package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import com.surofu.madeinrussia.core.repository.VendorFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorFaqRepository implements VendorFaqRepository {

    private final SpringDataVendorFaqRepository repository;

    @Override
    public List<VendorFaqView> getAllViewsByVendorDetailsId(Long id) {
        return repository.findAllViewsByVendorDetails_Id(id);
    }

    @Override
    public void save(VendorFaq vendorFaq) {
        repository.save(vendorFaq);
    }

    @Override
    public boolean existsByIdAndVendorId(Long faqId, Long vendorId) {
        return repository.existsByIdAndVendorDetailsUserId(faqId, vendorId);
    }
}
