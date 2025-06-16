package com.surofu.madeinrussia.core.service.productReview.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteProductReview {
    Long productId;
    Long productReviewId;
    SecurityUser securityUser;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Long productReviewId) {
            log.info("Successfully processed update product review with ID '{}'", productReviewId);
            return Success.INSTANCE;
        }

        static Result productReviewNotFound(Long productReviewId, Long productId) {
            log.warn("Product review with ID '{}' is product with ID '{}' not found", productReviewId, productId);
            return ProductReviewNotFound.of(productReviewId, productId);
        }

        static Result forbidden(Long productId, Long productReviewId, UserLogin currentUserLogin, UserLogin ownerUserLogin) {
            log.warn("""
                    Forbidden.
                    For product review with ID '{}' in product with ID '{}'
                    Current user with login '{}' is not owner with login '{}'
                    """, productReviewId, productId, currentUserLogin, ownerUserLogin);
            return Forbidden.INSTANCE;
        }

        static Result unauthorized() {
            log.warn("Unauthorized when processing update product review");
            return Unauthorized.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class ProductReviewNotFound implements Result {
            Long productReviewId;
            Long productId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processProductReviewNotFound(this);
            }
        }

        enum Unauthorized implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUnauthorized(this);
            }
        }

        enum Forbidden implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processForbidden(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processProductReviewNotFound(ProductReviewNotFound result);

            T processForbidden(Forbidden result);

            T processUnauthorized(Unauthorized result);
        }
    }
}
