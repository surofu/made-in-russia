package com.surofu.madeinrussia.core.service.productReview.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewContent;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewRating;
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

        static Result unauthorized() {
            log.warn("Unauthorized when processing create product review");
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
        class ProductNotFound implements Result {
            Long productId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processProductNotFound(this);
            }
        }

        enum Unauthorized implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUnauthorized(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processProductNotFound(ProductNotFound result);

            T processUnauthorized(Unauthorized result);
        }
    }
}
