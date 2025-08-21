package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.cache.ProductSummaryCacheManager;
import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.model.currency.CurrencyCode;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.repository.ProductSummaryViewRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductSummarySpecifications;
import com.surofu.madeinrussia.core.service.currency.CurrencyConverterService;
import com.surofu.madeinrussia.core.service.product.ProductSummaryService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPage;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPageByVendorId;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewsByIds;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSummaryApplicationService implements ProductSummaryService {
    private final ProductSummaryViewRepository productSummaryViewRepository;
    private final UserRepository userRepository;
    private final ProductSummaryCacheManager productSummaryCacheManager;
    private final LocalizationManager localizationManager;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public GetProductSummaryViewPage.Result getProductSummaryPage(GetProductSummaryViewPage operation) {
        // Check cache
        String hash = getFirstPageHash(operation);
        Page<ProductSummaryViewDto> cachedPage = productSummaryCacheManager.getFirstPage(hash);

        if (cachedPage != null) {
            return GetProductSummaryViewPage.Result.success(cachedPage);
        }

        // Process
        List<Long> allChildCategoriesIds = categoryRepository.getCategoriesIdsByIds(operation.getCategoryIds());
        List<Long> categoryIdsWithChildren = new ArrayList<>(allChildCategoriesIds);


        if (operation.getCategoryIds() != null) {
            categoryIdsWithChildren.addAll(operation.getCategoryIds());
        }

        Specification<ProductSummaryView> specification = Specification
                .where(ProductSummarySpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
                .and(ProductSummarySpecifications.hasCategories(categoryIdsWithChildren))
                .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()))
                .and(ProductSummarySpecifications.byTitle(operation.getTitle()));

        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), Sort.by("creationDate").descending());

        Page<ProductSummaryView> productSummaryViewPage = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);

        Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage
                .map(p -> ProductSummaryViewDto.of(
                        localizationManager.localizePrice(p, operation.getLocale()),
                        operation.getLocale().getLanguage())
                );

        if (validateOperationHash(operation)) {
            productSummaryCacheManager.setFirstPage(hash, productSummaryViewDtoPage);
        }

        return GetProductSummaryViewPage.Result.success(productSummaryViewDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductSummaryViewsByIds.Result getProductSummaryViewsByIds(GetProductSummaryViewsByIds operation) {
        List<ProductSummaryView> productSummaryViewList = productSummaryViewRepository.getProductSummaryViewByIds(operation.getProductIds());
        List<ProductSummaryViewDto> productSummaryViewDtoList = new ArrayList<>(productSummaryViewList.size());

        for (ProductSummaryView view : productSummaryViewList) {
            productSummaryViewDtoList.add(ProductSummaryViewDto.of(
                    localizationManager.localizePrice(view, operation.getLocale()),
                    operation.getLocale().getLanguage())
            );
        }

        return GetProductSummaryViewsByIds.Result.success(productSummaryViewDtoList);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductSummaryViewById.Result getProductSummaryViewById(GetProductSummaryViewById operation) {
        Long productSummaryId = operation.getProductSummaryId();
        Optional<ProductSummaryView> productSummaryView = productSummaryViewRepository.getProductSummaryViewById(productSummaryId);
        Optional<ProductSummaryViewDto> productSummaryViewDto = productSummaryView
                .map(p -> ProductSummaryViewDto.of(
                        localizationManager.localizePrice(p, operation.getLocale()),
                        operation.getLocale().getLanguage())
                );

        if (productSummaryViewDto.isPresent()) {
            return GetProductSummaryViewById.Result.success(productSummaryViewDto.get());
        }

        return GetProductSummaryViewById.Result.notFound(productSummaryId);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductSummaryViewPageByVendorId.Result getProductSummaryViewPageByVendorId(GetProductSummaryViewPageByVendorId operation) {
        if (!userRepository.existsVendorById(operation.getVendorId())) {
            return GetProductSummaryViewPageByVendorId.Result.vendorNotFound(operation.getVendorId());
        }

        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), Sort.by("creationDate").descending());

        List<Long> allChildCategoriesIds = categoryRepository.getCategoriesIdsByIds(operation.getCategoryIds());
        List<Long> categoryIdsWithChildren = new ArrayList<>(allChildCategoriesIds);

        if (operation.getCategoryIds() != null) {
            categoryIdsWithChildren.addAll(operation.getCategoryIds());
        }

        Specification<ProductSummaryView> specification = Specification
                .where(ProductSummarySpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
                .and(ProductSummarySpecifications.hasCategories(categoryIdsWithChildren))
                .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()))
                .and(ProductSummarySpecifications.byTitle(operation.getTitle()))
                .and(ProductSummarySpecifications.byVendorId(operation.getVendorId()));

        Page<ProductSummaryView> productSummaryViewPage = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
        Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage
                .map(p -> ProductSummaryViewDto.of(
                        localizationManager.localizePrice(p, operation.getLocale()),
                        operation.getLocale().getLanguage())
                );

        return GetProductSummaryViewPageByVendorId.Result.success(productSummaryViewDtoPage);
    }

    private String getFirstPageHash(GetProductSummaryViewPage operation) {
        return operation.getLocale().getLanguage() +
                operation.getPage() +
                operation.getSize() +
                operation.getTitle() +
                Objects.requireNonNullElse(operation.getCategoryIds(), "") +
                Objects.requireNonNullElse(operation.getDeliveryMethodIds(), "") +
                Objects.requireNonNullElse(operation.getMinPrice(), "") +
                Objects.requireNonNullElse(operation.getMaxPrice(), "");
    }

    private boolean validateOperationHash(GetProductSummaryViewPage operation) {
        boolean page = operation.getPage() >= 1 && operation.getPage() <= 10;
        boolean size = operation.getSize() == 10;
        boolean title = operation.getTitle() == null || operation.getTitle().isEmpty();
        boolean category = operation.getCategoryIds() == null || operation.getCategoryIds().isEmpty();
        boolean deliveryMethod = operation.getDeliveryMethodIds() == null || operation.getDeliveryMethodIds().isEmpty();
        boolean minPrice = operation.getMinPrice() == null || operation.getMinPrice().compareTo(BigDecimal.valueOf(100)) <= 0;
        boolean maxPrice = operation.getMaxPrice() == null || operation.getMaxPrice().compareTo(BigDecimal.valueOf(1_000_000)) <= 0;
        return page && size && title && category && deliveryMethod && minPrice && maxPrice;
    }
}
