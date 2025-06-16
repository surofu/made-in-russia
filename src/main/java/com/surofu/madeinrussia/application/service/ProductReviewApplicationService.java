package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductReviewDto;
import com.surofu.madeinrussia.application.service.async.AsyncProductReviewApplicationService;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.productReview.operation.CreateProductReview;
import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;
import com.surofu.madeinrussia.core.service.productReview.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewApplicationService implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final AsyncProductReviewApplicationService asyncProductReviewApplicationService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productReviewPageByProductId",
            key = """
                    {
                    #operation.productId,
                    #operation.page
                    }
                    """,
            unless = """
                    {
                    #result.getProductReviewDtoPage().isEmpty()
                    or #operation.size != null
                    or #operation.minRating != null
                    or #operation.maxRating != null
                    }
                    """
    )
    public GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), Sort.by("creationDate").descending());

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

    @Override
    @Transactional
    @CacheEvict(
            value = "productReviewPageByProductId",
            condition = "#result instanceof T(com.surofu.madeinrussia.core.service.productReview.operation.CreateProductReview$Result$ProductNotFound)"
    )
    public CreateProductReview.Result createProductReview(CreateProductReview operation) {
        Optional<User> user = userRepository.getUserByEmail(operation.getSecurityUser().getUser().getEmail());

        if (user.isEmpty()) {
            return CreateProductReview.Result.unauthorized();
        }

        Optional<Product> product = productRepository.getProductById(operation.getProductId());

        if (product.isEmpty()) {
            return CreateProductReview.Result.productNotFound(operation.getProductId());
        }

        ProductReview productReview = new ProductReview();
        productReview.setUser(user.get());
        productReview.setProduct(product.get());
        productReview.setContent(operation.getProductReviewContent());
        productReview.setRating(operation.getProductReviewRating());

        asyncProductReviewApplicationService.saveProductReview(productReview);

        return CreateProductReview.Result.success(productReview);
    }
}
