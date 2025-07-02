package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductReviewDto;
import com.surofu.madeinrussia.application.service.async.AsyncProductReviewApplicationService;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorView.VendorView;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.repository.VendorViewRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.product.review.operation.CreateProductReview;
import com.surofu.madeinrussia.core.service.product.review.operation.DeleteProductReview;
import com.surofu.madeinrussia.core.service.product.review.operation.GetProductReviewPageByProductId;
import com.surofu.madeinrussia.core.service.product.review.ProductReviewService;
import com.surofu.madeinrussia.core.service.product.review.operation.UpdateProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
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
    private final VendorViewRepository vendorViewRepository;

    private final AsyncProductReviewApplicationService asyncProductReviewApplicationService;

    @Override
    @Transactional(readOnly = true)
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
    public CreateProductReview.Result createProductReview(CreateProductReview operation) {
        User user = operation.getSecurityUser().getUser();

        if (user.getRegistrationDate().getValue().isAfter(ZonedDateTime.now().minusWeeks(1))) {
            return CreateProductReview.Result.accountIsTooYoung(user.getEmail());
        }

        Optional<Product> product = productRepository.getProductById(operation.getProductId());

        if (product.isEmpty()) {
            return CreateProductReview.Result.productNotFound(operation.getProductId());
        }

        VendorView vendorView = new VendorView();
        vendorView.setUser(user);
        vendorView.setVendorDetails(product.get().getUser().getVendorDetails());

        boolean viewIsNotExists = vendorViewRepository.notExists(vendorView);

        if (viewIsNotExists) {
            return CreateProductReview.Result.vendorProfileNotViewed(user.getEmail());
        }

        ProductReview productReview = new ProductReview();
        productReview.setUser(user);
        productReview.setProduct(product.get());
        productReview.setContent(operation.getProductReviewContent());
        productReview.setRating(operation.getProductReviewRating());

        asyncProductReviewApplicationService.saveProductReview(productReview);

        return CreateProductReview.Result.success(productReview);
    }

    @Override
    @Transactional
    public UpdateProductReview.Result updateProductReview(UpdateProductReview operation) {
        Optional<User> user = userRepository.getUserByEmail(operation.getSecurityUser().getUser().getEmail());

        if (user.isEmpty()) {
            return UpdateProductReview.Result.unauthorized();
        }

        Optional<ProductReview> productReview = productReviewRepository.findById(operation.getProductReviewId());

        if (productReview.isEmpty()) {
            return UpdateProductReview.Result.productReviewNotFound(operation.getProductReviewId(), operation.getProductId());
        }

        if (productReviewRepository.isUserOwnerOfProductReview(user.get().getId(), operation.getProductReviewId())) {
            if (operation.getProductReviewContent() != null) {
                productReview.get().setContent(operation.getProductReviewContent());
            }

            if (operation.getProductReviewRating() != null) {
                productReview.get().setRating(operation.getProductReviewRating());
            }

            asyncProductReviewApplicationService.saveProductReview(productReview.get());

            return UpdateProductReview.Result.success(productReview.get());
        }

        return UpdateProductReview.Result.forbidden(
                operation.getProductId(),
                operation.getProductReviewId(),
                user.get().getLogin(),
                productReview.get().getUser().getLogin()
        );
    }

    @Override
    @Transactional
    public DeleteProductReview.Result deleteProductReview(DeleteProductReview operation) {
        Optional<User> user = userRepository.getUserByEmail(operation.getSecurityUser().getUser().getEmail());

        if (user.isEmpty()) {
            return DeleteProductReview.Result.unauthorized();
        }

        Optional<ProductReview> productReview = productReviewRepository.findById(operation.getProductReviewId());

        if (productReview.isEmpty()) {
            return DeleteProductReview.Result.productReviewNotFound(operation.getProductReviewId(), operation.getProductId());
        }

        if (!productReviewRepository.isUserOwnerOfProductReview(user.get().getId(), operation.getProductReviewId())) {
            asyncProductReviewApplicationService.deleteProductReviewById(operation.getProductReviewId());

            return DeleteProductReview.Result.success(operation.getProductReviewId());
        }

        return DeleteProductReview.Result.forbidden(
                operation.getProductId(),
                operation.getProductReviewId(),
                user.get().getLogin(),
                productReview.get().getUser().getLogin()
        );
    }
}
