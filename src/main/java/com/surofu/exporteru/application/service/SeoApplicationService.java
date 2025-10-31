package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.SeoCacheManager;
import com.surofu.exporteru.application.dto.seo.SeoDto;
import com.surofu.exporteru.application.dto.seo.SeoProductDto;
import com.surofu.exporteru.application.dto.seo.SeoVendorDto;
import com.surofu.exporteru.core.repository.SeoRepository;
import com.surofu.exporteru.core.service.seo.SeoService;
import com.surofu.exporteru.core.service.seo.operation.GetSeo;
import com.surofu.exporteru.infrastructure.persistence.seo.SeoProductView;
import com.surofu.exporteru.infrastructure.persistence.seo.SeoVendorView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoApplicationService implements SeoService {
    private final SeoRepository repository;
    private final SeoCacheManager cacheManager;

    @Override
    @Transactional(readOnly = true)
    public GetSeo.Result getSeo() {
        if (cacheManager.contains()) {
            return GetSeo.Result.success(cacheManager.get());
        }

        List<SeoProductView> productViews = repository.getSeoProducts();
        List<SeoVendorView> vendorViews = repository.getSeoVendors();

        List<SeoProductDto> productDtos = productViews.stream().map(SeoProductDto::of).toList();
        List<SeoVendorDto> vendorDtos = vendorViews.stream().map(SeoVendorDto::of).toList();

        SeoDto seoDto = new SeoDto(productDtos, vendorDtos);
        cacheManager.set(seoDto);
        return GetSeo.Result.success(seoDto);
    }
}
