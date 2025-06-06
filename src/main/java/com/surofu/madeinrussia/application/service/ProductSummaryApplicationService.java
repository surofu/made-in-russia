package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductSummaryViewDto;
import com.surofu.madeinrussia.core.repository.ProductSummaryViewRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductSummarySpecifications;
import com.surofu.madeinrussia.core.service.product.ProductSummaryService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPage;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSummaryApplicationService implements ProductSummaryService {
    private final ProductSummaryViewRepository productSummaryViewRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productSummaryViewPage",
            key = """
                    {
                     #operation.getPage(), #operation.getSize(),
                     #operation.getTitle(),
                     #operation.getDeliveryMethodIds()?.hashCode(),
                     #operation.getCategoryIds()?.hashCode(),
                     #operation.getMinPrice(), #operation.getMaxPrice()
                     }
                    """,
            unless = "#result.getProductSummaryViewDtoPage().isEmpty()"
    )
    public GetProductSummaryViewPage.Result getProductSummaryPage(GetProductSummaryViewPage operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

        Specification<ProductSummaryView> specification = Specification
                .where(ProductSummarySpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
                .and(ProductSummarySpecifications.hasCategories(operation.getCategoryIds()))
                .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()))
                .and(ProductSummarySpecifications.byTitle(operation.getTitle()));

        Page<ProductSummaryView> productSummaryViewPage = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
        Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage.map(ProductSummaryViewDto::of);

        return GetProductSummaryViewPage.Result.success(productSummaryViewDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productSummaryView",
            key = "#operation.getProductSummaryId()",
            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById$Result$NotFound)"
    )
    public GetProductSummaryViewById.Result getProductSummaryViewById(GetProductSummaryViewById operation) {
        Long productSummaryId = operation.getProductSummaryId();
        Optional<ProductSummaryView> productSummaryView = productSummaryViewRepository.getProductSummaryViewById(productSummaryId);
        Optional<ProductSummaryViewDto> productSummaryViewDto = productSummaryView.map(ProductSummaryViewDto::of);

        if (productSummaryViewDto.isPresent()) {
            return GetProductSummaryViewById.Result.success(productSummaryViewDto.get());
        }

        return GetProductSummaryViewById.Result.notFound(productSummaryId);
    }
}
