package com.surofu.madeinrussia.infrastructure.persistence.vendor.site;

import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSite;
import com.surofu.madeinrussia.core.repository.VendorSiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorSiteRepository implements VendorSiteRepository {

    private final SpringDataVendorSiteRepository repository;

    @Override
    public List<VendorSite> getAllByVendorDetailsId(Long id) {
        return repository.findAllByVendorDetails_Id(id);
    }
}
