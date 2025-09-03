package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.service.moderation.ModerationService;
import com.surofu.madeinrussia.core.service.moderation.operation.SetProductReviewApproveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModerationApplicationService implements ModerationService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public SetProductReviewApproveStatus.Result setProductReviewApproveStatus(SetProductReviewApproveStatus operation) {
        Optional<ProductReview> productReviewOptional = productReviewRepository.getById(operation.getId());

        if (productReviewOptional.isEmpty()) {
            return SetProductReviewApproveStatus.Result.notFound(operation.getId());
        }

        ProductReview productReview = productReviewOptional.get();

        if (operation.getApproveStatus().equals(ApproveStatus.REJECTED)) {
            try {
                productReviewRepository.delete(productReview);
                return SetProductReviewApproveStatus.Result.success(operation.getId(), operation.getApproveStatus());
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return SetProductReviewApproveStatus.Result.saveError(operation.getId(), e);
            }
        }

        productReview.setApproveStatus(operation.getApproveStatus());

        try {
            productReviewRepository.save(productReview);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return SetProductReviewApproveStatus.Result.saveError(operation.getId(), e);
        }

        return SetProductReviewApproveStatus.Result.success(operation.getId(), operation.getApproveStatus());
    }
}
