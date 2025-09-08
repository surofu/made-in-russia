package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import com.surofu.madeinrussia.application.annotation.Bench;
import com.surofu.madeinrussia.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.madeinrussia.core.repository.VendorFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorFaqRepository implements VendorFaqRepository {

    private final SpringDataVendorFaqRepository repository;

    @Override
    public List<VendorFaqView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang) {
        return repository.findAllViewsByVendorDetailsIdAndLang(id, lang);
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
