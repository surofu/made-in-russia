package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.infrastructure.persistence.seo.SeoProductView;
import com.surofu.exporteru.infrastructure.persistence.seo.SeoVendorView;

import java.util.List;

public interface SeoRepository {
    List<SeoProductView> getSeoProducts();

    List<SeoVendorView> getSeoVendors();
}
