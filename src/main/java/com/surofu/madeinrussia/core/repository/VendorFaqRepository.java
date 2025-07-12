package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.faq.VendorFaqView;

import java.util.List;

public interface VendorFaqRepository {
    List<VendorFaqView> getAllViewsByVendorDetailsId(Long id);

    void save(VendorFaq vendorFaq);

    boolean existsByIdAndVendorId(Long faqId, Long vendorId);
}
