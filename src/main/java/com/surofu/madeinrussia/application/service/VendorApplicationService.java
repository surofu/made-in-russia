package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.VendorDto;
import com.surofu.madeinrussia.application.dto.page.VendorReviewPageDto;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.vendor.VendorService;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorById;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorApplicationService implements VendorService {

    private final UserRepository userRepository;
    private final ProductReviewRepository productReviewRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "vendorById",
            key = "#operation.vendorId"
    )
    public GetVendorById.Result getVendorById(GetVendorById operation) {
        Optional<User> user = userRepository.getVendorById(operation.getVendorId());
        Optional<VendorDto> vendorDto = user.map(VendorDto::of);

        if (vendorDto.isPresent()) {
            return GetVendorById.Result.success(vendorDto.get());
        }

        return GetVendorById.Result.notFound(operation.getVendorId());
    }

    @Override
    public GetVendorReviewPageById.Result getVendorReviewPageById(GetVendorReviewPageById operation) {
        if (userRepository.existsVendorById(operation.getVendorId())) {
            Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

            Specification<ProductReview> specification = Specification
                    .where(ProductReviewSpecifications.byProductUserId(operation.getVendorId()))
                    .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(), operation.getMaxRating()));

            Page<ProductReview> productReviewPage = productReviewRepository.findAll(specification, pageable);

            if (productReviewPage.getContent().isEmpty()) {
                VendorReviewPageDto vendorReviewPageDto = VendorReviewPageDto.of(productReviewPage, 0);

                return GetVendorReviewPageById.Result.success(vendorReviewPageDto);
            }

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

            Double ratingAverage = productReviewRepository.findAverageRatingByVendorId(operation.getVendorId());

            VendorReviewPageDto vendorReviewPageDto = VendorReviewPageDto.of(productReviewPageWithMedia, ratingAverage);

            return GetVendorReviewPageById.Result.success(vendorReviewPageDto);
        }

        return GetVendorReviewPageById.Result.vendorNotFound(operation.getVendorId());
    }
}
