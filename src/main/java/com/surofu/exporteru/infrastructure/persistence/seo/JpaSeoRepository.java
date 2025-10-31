package com.surofu.exporteru.infrastructure.persistence.seo;

import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.repository.SeoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaSeoRepository implements SeoRepository {
    private final SpringDataSeoProductRepository productRepository;
    private final SpringDataSeoVendorRepository vendorRepository;

    @Override
    public List<SeoProductView> getSeoProducts() {
        return productRepository.findAllBy(ApproveStatus.APPROVED.name());
    }

    @Override
    public List<SeoVendorView> getSeoVendors() {
        return vendorRepository.findAllBy(true, UserRole.ROLE_VENDOR.name());
    }
}
