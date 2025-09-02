package com.surofu.madeinrussia.infrastructure.persistence.vendor.media;

import com.surofu.madeinrussia.core.model.vendorDetails.media.VendorMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataVendorMediaRepository extends JpaRepository<VendorMedia, Long> {
    List<VendorMedia> findAllByVendorDetailsIdOrderByPositionAsc(Long vendorDetailsId);
}
