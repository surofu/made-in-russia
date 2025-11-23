package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.GeneralCacheService;
import com.surofu.exporteru.application.dto.GeneralDto;
import com.surofu.exporteru.application.dto.advertisement.AdvertisementDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.product.ProductSummaryViewDto;
import com.surofu.exporteru.application.utils.CategoryUtils;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.advertisement.Advertisement;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.repository.AdvertisementRepository;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.ProductSummaryViewRepository;
import com.surofu.exporteru.core.repository.specification.AdvertisementSpecifications;
import com.surofu.exporteru.core.service.general.GeneralService;
import com.surofu.exporteru.core.service.general.operation.GetAllGeneral;
import com.surofu.exporteru.core.view.ProductSummaryView;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    Page<ProductSummaryView> page =
        productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
    Page<ProductSummaryViewDto> pageDto = page.map(view -> ProductSummaryViewDto.of(
        localizationManager.localizePrice(view, operation.getLocale()),
        operation.getLocale().getLanguage()
    ));

    // Categories
    List<Category> categories = categoryRepository.getAll();
    List<CategoryDto> categoryDtoList = CategoryUtils.buildTree(categories, operation.getLocale());

    List<CategoryDto> categoryL1L2DtoList = categoryDtoList.stream()
        .map(c -> {
          var copy = c.copy();
          if (copy.getChildren() != null && !copy.getChildren().isEmpty()) {
            copy.getChildren().forEach(child -> child.setChildren(Collections.emptyList()));
          }
          return copy;
        }).toList();

    // Advertisements
    Sort sort = Sort.by("creationDate").descending();
    Specification<Advertisement> advertisementSpec = AdvertisementSpecifications.byNotExpiredDate();
    List<Advertisement> advertisements = advertisementRepository.getAll(advertisementSpec, sort);
    List<AdvertisementDto> advertisementDtos = advertisements.stream()
        .map(a -> AdvertisementDto.of(a, operation.getLocale()))
        .toList();

    GeneralDto generalDto =
        new GeneralDto(pageDto, categoryL1L2DtoList, categoryDtoList, advertisementDtos);

    // Set cache
    generalCacheService.set(operation.getLocale(), generalDto);
    return GetAllGeneral.Result.success(generalDto);
  }
}
