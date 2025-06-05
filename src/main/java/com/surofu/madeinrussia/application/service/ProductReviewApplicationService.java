package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductReviewDto;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;
import com.surofu.madeinrussia.core.service.productReview.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewApplicationService implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productReviewPageByProductId",
            key = """
                    {
                     #operation.getProductId(),
                     #operation.getPage(), #operation.getSize(),
                     #operation.getMinRating(), #operation.getMaxRating()
                     }
                    """,
            unless = "#result.getProductReviewDtoPage().isEmpty()"
    )
    public GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

        Specification<ProductReview> specification = Specification
                .where(ProductReviewSpecifications.byProductId(operation.getProductId()))
                .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(), operation.getMaxRating()));

        Page<ProductReview> productReviewPage = productReviewRepository.findAll(specification, pageable);

        if (!productReviewPage.getContent().isEmpty()) {
            List<Long> reviewIds = productReviewPage.getContent().stream()
                    .map(ProductReview::getId)
                    .toList();

            List<ProductReview> reviewsWithMedia = productReviewRepository.findByIdInWithMedia(reviewIds);

            Map<Long, ProductReview> reviewMap = reviewsWithMedia.stream()
                    .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

            Page<ProductReview> productReviewPageWithMedia = productReviewPage.map(p -> {
               p.setMedia(reviewMap.get(p.getId()).getMedia());
               return p;
            });

            Page<ProductReviewDto> productReviewDtoPage = productReviewPageWithMedia.map(ProductReviewDto::of);

            return GetProductReviewPageByProductId.Result.success(productReviewDtoPage);
        }

        Page<ProductReviewDto> productReviewDtoPage = productReviewPage.map(ProductReviewDto::of);
        return GetProductReviewPageByProductId.Result.success(productReviewDtoPage);
    }
}
