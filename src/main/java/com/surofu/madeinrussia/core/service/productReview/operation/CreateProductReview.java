package com.surofu.madeinrussia.core.service.productReview.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewContent;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewRating;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class CreateProductReview {
    Long productId;
    SecurityUser securityUser;
    ProductReviewContent productReviewContent;
    ProductReviewRating productReviewRating;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(ProductReview productReview) {
            log.info("Successfully processed create product review: {}", productReview);
            return Success.INSTANCE;
        }

        static Result productNotFound(Long productId) {
            log.warn("Product with ID '{}' not found", productId);
            return ProductNotFound.of(productId);
        }

        static Result vendorProfileNotViewed(UserEmail userEmail) {
            log.warn("VendorProfile not viewed by user with email '{}' when processing create product review", userEmail.toString());
            return VendorProfileNotViewed.INSTANCE;
        }

        static Result accountIsTooYoung(UserEmail userEmail) {
            log.warn("Account with email '{}' is too young when processing create product review", userEmail.toString());
            return AccountIsTooYoung.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class ProductNotFound implements Result {
            Long productId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processProductNotFound(this);
            }
        }

        enum VendorProfileNotViewed implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processVendorProfileNotViewed(this);
            }
        }

        enum AccountIsTooYoung implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processAccountIsTooYoung(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processProductNotFound(ProductNotFound result);

            T processVendorProfileNotViewed(VendorProfileNotViewed result);

            T processAccountIsTooYoung(AccountIsTooYoung result);
        }
    }
}
