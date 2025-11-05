package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.GeneralCacheService;
import com.surofu.exporteru.application.cache.ProductSummaryCacheManager;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.product.review.media.ProductReviewMedia;
import com.surofu.exporteru.core.model.product.review.media.ProductReviewMediaUrl;
import com.surofu.exporteru.core.repository.*;
import com.surofu.exporteru.core.service.moderation.ModerationService;
import com.surofu.exporteru.core.service.moderation.operation.SetProductApproveStatus;
import com.surofu.exporteru.core.service.moderation.operation.SetProductReviewApproveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModerationApplicationService implements ModerationService {

    private final ProductRepository productRepository;

    private final ProductReviewRepository productReviewRepository;
    private final FileStorageRepository fileStorageRepository;
    private final ProductSummaryCacheManager productSummaryCacheManager;
    private final GeneralCacheService generalCacheService;

    @Override
    @Transactional
    public SetProductApproveStatus.Result setProductApproveStatus(SetProductApproveStatus operation) {
        Optional<Product> productOptional = productRepository.getProductByIdWithAnyApproveStatus(operation.getId());

        if (productOptional.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return SetProductApproveStatus.Result.notFound(operation.getId());
        }

        Product product = productOptional.get();
        product.setApproveStatus(operation.getApproveStatus());

        try {
            productRepository.save(product);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return SetProductApproveStatus.Result.saveError(operation.getId(), e);
        }


        productSummaryCacheManager.clearAll();
        generalCacheService.clear();
        return SetProductApproveStatus.Result.success(operation.getId(), operation.getApproveStatus());
    }

    @Override
    @Transactional
    public SetProductReviewApproveStatus.Result setProductReviewApproveStatus(SetProductReviewApproveStatus operation) {
        Optional<ProductReview> productReviewOptional = productReviewRepository.getByIdWithAnyApproveStatus(operation.getId());

        if (productReviewOptional.isEmpty()) {
            return SetProductReviewApproveStatus.Result.notFound(operation.getId());
        }

        ProductReview productReview = productReviewOptional.get();

        if (operation.getApproveStatus().equals(ApproveStatus.REJECTED)) {
            try {
                productReviewRepository.delete(productReview);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return SetProductReviewApproveStatus.Result.saveError(operation.getId(), e);
            }

            try {
                String[] links = productReview.getMedia().stream()
                        .map(ProductReviewMedia::getUrl)
                        .map(ProductReviewMediaUrl::toString)
                        .toArray(String[]::new);
                fileStorageRepository.deleteMediaByLink(links);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return SetProductReviewApproveStatus.Result.saveError(operation.getId(), e);
            }

            return SetProductReviewApproveStatus.Result.success(operation.getId(), operation.getApproveStatus());
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
