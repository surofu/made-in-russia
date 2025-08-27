package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.cache.GeneralCacheService;
import com.surofu.madeinrussia.application.dto.GeneralDto;
import com.surofu.madeinrussia.application.dto.advertisement.AdvertisementDto;
import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import com.surofu.madeinrussia.application.utils.CategoryUtils;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import com.surofu.madeinrussia.core.repository.AdvertisementRepository;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.repository.ProductSummaryViewRepository;
import com.surofu.madeinrussia.core.service.general.GeneralService;
import com.surofu.madeinrussia.core.service.general.operation.GetAllGeneral;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementView;
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneralApplicationService implements GeneralService {

    private final ProductSummaryViewRepository productSummaryViewRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertisementRepository advertisementRepository;
    private final LocalizationManager localizationManager;
    private final GeneralCacheService generalCacheService;

    @Override
    @Transactional(readOnly = true)
    public GetAllGeneral.Result getAll(GetAllGeneral operation) {
        // Check cache
        if (generalCacheService.exists(operation.getLocale())) {
            return GetAllGeneral.Result.success(generalCacheService.get(operation.getLocale()));
        }

        // Products
        Specification<ProductSummaryView> specification = Specification.where(null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());
        Page<ProductSummaryView> page = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
        Page<ProductSummaryViewDto> pageDto = page.map(view -> ProductSummaryViewDto.of(
                localizationManager.localizePrice(view, operation.getLocale()),
                operation.getLocale().getLanguage()
        ));

        // Categories
        List<CategoryView> categories = categoryRepository.getAllCategoriesViewsByLang(operation.getLocale().getLanguage());
        List<CategoryDto> categoryDtoList = CategoryUtils.buildTreeFromViews(categories);

        List<CategoryDto> categoryL1L2DtoList = categoryDtoList.stream()
                .map(c -> {
                    var copy = c.copy();
                    if (copy.getChildren() != null && !copy.getChildren().isEmpty()) {
                        copy.getChildren().forEach(child -> child.setChildren(Collections.emptyList()));
                    }
                    return copy;
                }).toList();

        // Advertisements
        List<AdvertisementView> advertisementViewList = advertisementRepository.getAllViewsByLang(operation.getLocale().getLanguage());
        List<AdvertisementDto> advertisementDtoList = advertisementViewList.stream().map(AdvertisementDto::of).toList();

        GeneralDto generalDto = new GeneralDto(pageDto, categoryL1L2DtoList, categoryDtoList, advertisementDtoList);

        // Set cache
        generalCacheService.set(operation.getLocale(), generalDto);
        return GetAllGeneral.Result.success(generalDto);
    }
}
