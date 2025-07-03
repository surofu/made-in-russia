package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.VendorDto;
import com.surofu.madeinrussia.application.dto.page.VendorReviewPageDto;
import com.surofu.madeinrussia.application.service.async.AsyncVendorViewApplicationService;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorView.VendorView;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.repository.VendorFaqRepository;
import com.surofu.madeinrussia.core.repository.VendorViewRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.vendor.VendorService;
import com.surofu.madeinrussia.core.service.vendor.operation.CreateVendorFaq;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorById;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorApplicationService implements VendorService {

    private final UserRepository userRepository;
    private final VendorViewRepository vendorViewRepository;
    private final ProductReviewRepository productReviewRepository;
    private final VendorFaqRepository vendorFaqRepository;

    private final AsyncVendorViewApplicationService asyncVendorViewApplicationService;

    @Override
    @Transactional
    public GetVendorById.Result getVendorById(GetVendorById operation) {
        Optional<User> vendor = userRepository.getVendorById(operation.getVendorId());

        if (vendor.isPresent()) {
            Long viewsCount = vendorViewRepository.getCountByVendorDetailsId(vendor.get().getVendorDetails().getId());
            vendor.get().getVendorDetails().setVendorViewsCount(viewsCount);
            Optional<VendorDto> vendorDto = vendor.map(VendorDto::of);

            if (operation.getSecurityUser().isPresent() &&
                    !operation.getSecurityUser().get().getUser().getId().equals(vendor.get().getId())) {
                VendorView vendorView = new VendorView();
                vendorView.setVendorDetails(vendor.get().getVendorDetails());
                vendorView.setUser(operation.getSecurityUser().get().getUser());
                asyncVendorViewApplicationService.saveVendorViewInDatabase(vendorView);
            }

            if (vendorDto.isPresent()) {
                return GetVendorById.Result.success(vendorDto.get());
            }
        }

        return GetVendorById.Result.notFound(operation.getVendorId());
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public CreateVendorFaq.Result createVendorFaq(CreateVendorFaq operation) {
        VendorDetails vendorDetails = operation.getSecurityUser().getUser().getVendorDetails();

        VendorFaq faq = new VendorFaq();
        faq.setVendorDetails(vendorDetails);
        faq.setQuestion(operation.getQuestion());
        faq.setAnswer(operation.getAnswer());

        vendorFaqRepository.save(faq);

        return CreateVendorFaq.Result.success();
    }
}
