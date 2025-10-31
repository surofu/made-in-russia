package com.surofu.exporteru.core.service.product.review.operation;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.product.review.ProductReviewContent;
import com.surofu.exporteru.core.model.product.review.ProductReviewRating;
import com.surofu.exporteru.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class CreateProductReview {
    Long productId;
    SecurityUser securityUser;
    ProductReviewContent productReviewContent;
    ProductReviewRating productReviewRating;
    List<MultipartFile> media;

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

        static Result saveError(Exception e) {
            log.error("Error saving product review", e);
            return SaveError.INSTANCE;
        }

        static Result tooManyReviews(UserEmail email) {
            log.warn("Too many reviews when processing create product review, user email '{}'", email);
            return TooManyReviews.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.error("Translation error when processing create product review", e);
            return TranslationError.INSTANCE;
        }

        static Result uploadError(Exception e) {
            log.error("Upload error when processing create product review", e);
            return UploadError.INSTANCE;
        }

        static Result emptyFile() {
            log.warn("Empty file when processing create product review");
            return EmptyFile.INSTANCE;
        }

        static Result invalidContentType(String contentType) {
            log.warn("Invalid content type when processing create product review: {}", contentType);
            return InvalidContentType.of(contentType);
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

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        enum TooManyReviews implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processToManyReviews(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum UploadError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUploadError(this);
            }
        }

        enum EmptyFile implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyFile(this);
            }
        }

        @Value(staticConstructor = "of")
        class InvalidContentType implements Result {
            String contentType;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidContentType(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processProductNotFound(ProductNotFound result);

            T processVendorProfileNotViewed(VendorProfileNotViewed result);

            T processAccountIsTooYoung(AccountIsTooYoung result);

            T processSaveError(SaveError result);

            T processToManyReviews(TooManyReviews result);

            T processTranslationError(TranslationError result);

            T processUploadError(UploadError result);

            T processEmptyFile(EmptyFile result);

            T processInvalidContentType(InvalidContentType result);
        }
    }
}
