package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncProductReviewApplicationService {

    private final ProductReviewRepository productReviewRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductReview(ProductReview productReview) {
        try {
            productReviewRepository.save(productReview);
        } catch (Exception e) {
            log.error("Error while saving product review: {}", e.getMessage(), e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProductReviewById(Long productReviewId) {
        try {
            productReviewRepository.deleteById(productReviewId);
        } catch (Exception e) {
            log.error("Error while deleting product review: {}", e.getMessage(), e);
        }
    }
}
