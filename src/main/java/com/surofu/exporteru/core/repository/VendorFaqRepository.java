package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.exporteru.infrastructure.persistence.vendor.faq.VendorFaqView;

import java.util.List;
import java.util.Optional;

public interface VendorFaqRepository {

    List<VendorFaqView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang);

    VendorFaq save(VendorFaq vendorFaq);

    Optional<VendorFaq> findByIdAndVendorId(Long faqId, Long vendorId);

    Optional<VendorFaq> getByIdAndVendorDetailsId(Long id, Long venndorDetailsId);

    void delete(VendorFaq faq);

    Optional<VendorFaq> findById(Long faqId);
}
