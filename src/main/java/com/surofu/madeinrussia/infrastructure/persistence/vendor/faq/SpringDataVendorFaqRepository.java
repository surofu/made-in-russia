package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorFaqRepository extends JpaRepository<VendorFaq, Long> {

    List<VendorFaqView> findAllViewsByVendorDetails_Id(Long id);

    boolean existsByIdAndVendorDetailsUserId(Long faqId, Long vendorId);
}
