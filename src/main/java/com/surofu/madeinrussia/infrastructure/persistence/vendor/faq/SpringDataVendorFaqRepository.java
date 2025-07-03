package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataVendorFaqRepository extends JpaRepository<VendorFaq, Long> {
}
