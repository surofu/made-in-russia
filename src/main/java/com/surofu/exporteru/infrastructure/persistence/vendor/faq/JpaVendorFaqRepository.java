package com.surofu.exporteru.infrastructure.persistence.vendor.faq;

import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.exporteru.core.repository.VendorFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaVendorFaqRepository implements VendorFaqRepository {

    private final SpringDataVendorFaqRepository repository;

    @Override
    public List<VendorFaqView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang) {
        return repository.findAllViewsByVendorDetailsIdAndLang(id, lang);
    }

    @Override
    public Optional<VendorFaq> getByIdAndVendorDetailsId(Long id, Long venndorDetailsId) {
        return repository.findByIdAndVendorDetails_Id(id, venndorDetailsId);
    }

    @Override
    public VendorFaq save(VendorFaq vendorFaq) {
        return repository.save(vendorFaq);
    }

    @Override
    public Optional<VendorFaq> findByIdAndVendorId(Long faqId, Long vendorId) {
        return repository.findByIdAndVendorDetailsUserId(faqId, vendorId);
    }

    @Override
    public void delete(VendorFaq faq) {
        repository.delete(faq);
    }

    @Override
    public Optional<VendorFaq> findById(Long faqId) {
        return repository.findById(faqId);
    }
}
