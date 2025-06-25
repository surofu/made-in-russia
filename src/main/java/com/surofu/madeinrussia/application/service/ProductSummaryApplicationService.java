package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductSummaryViewDto;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.repository.ProductSummaryViewRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductSummarySpecifications;
import com.surofu.madeinrussia.core.service.product.ProductSummaryService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPage;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPageByVendorId;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSummaryApplicationService implements ProductSummaryService {
    private final ProductSummaryViewRepository productSummaryViewRepository;
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public GetProductSummaryViewPage.Result getProductSummaryPage(GetProductSummaryViewPage operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), Sort.by("creationDate").descending());

        List<Long> allChildCategoriesIds = categoryRepository.getCategoriesIdsByIds(operation.getCategoryIds());
        List<Long> categoryIdsWithChildren = new ArrayList<>();

        if (operation.getCategoryIds() != null) {
            categoryIdsWithChildren.addAll(operation.getCategoryIds());
        }

        categoryIdsWithChildren.addAll(allChildCategoriesIds);

        Specification<ProductSummaryView> specification = Specification
                .where(ProductSummarySpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
                .and(ProductSummarySpecifications.hasCategories(categoryIdsWithChildren))
                .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()))
                .and(ProductSummarySpecifications.byTitle(operation.getTitle()));

        Page<ProductSummaryView> productSummaryViewPage = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
        Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage.map(ProductSummaryViewDto::of);

        return GetProductSummaryViewPage.Result.success(productSummaryViewDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductSummaryViewById.Result getProductSummaryViewById(GetProductSummaryViewById operation) {
        Long productSummaryId = operation.getProductSummaryId();
        Optional<ProductSummaryView> productSummaryView = productSummaryViewRepository.getProductSummaryViewById(productSummaryId);
        Optional<ProductSummaryViewDto> productSummaryViewDto = productSummaryView.map(ProductSummaryViewDto::of);

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
        List<Long> categoryIdsWithChildren = new ArrayList<>();

        if (operation.getCategoryIds() != null) {
            categoryIdsWithChildren.addAll(operation.getCategoryIds());
        }

        categoryIdsWithChildren.addAll(allChildCategoriesIds);

        Specification<ProductSummaryView> specification = Specification
                .where(ProductSummarySpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
                .and(ProductSummarySpecifications.hasCategories(categoryIdsWithChildren))
                .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()))
                .and(ProductSummarySpecifications.byTitle(operation.getTitle()))
                .and(ProductSummarySpecifications.byVendorId(operation.getVendorId()));

        Page<ProductSummaryView> productSummaryViewPage = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
        Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage.map(ProductSummaryViewDto::of);

        return GetProductSummaryViewPageByVendorId.Result.success(productSummaryViewDtoPage);
    }
}
