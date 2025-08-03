package com.surofu.madeinrussia.core.service.product.review.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewContent;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewRating;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateProductReview {
    Long productId;
    Long productReviewId;
    SecurityUser securityUser;
    ProductReviewContent productReviewContent;
    ProductReviewRating productReviewRating;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(ProductReview productReview) {
            log.info("Successfully processed update product review: {}", productReview);
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

        static Result saveError(Exception e) {
            log.error("Error saving product review", e);
            return SaveError.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.error("Translation error when processing create product review", e);
            return TranslationError.INSTANCE;
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

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processProductReviewNotFound(ProductReviewNotFound result);

            T processForbidden(Forbidden result);

            T processUnauthorized(Unauthorized result);

            T processSaveError(SaveError result);

            T processTranslationError(TranslationError result);
        }
    }
}
