package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.command.product.update.*;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.product.ProductDescription;
import com.surofu.madeinrussia.core.model.product.ProductTitle;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateProduct {
    Long productId;
    SecurityUser securityUser;
    ProductTitle productTitle;
    ProductDescription productDescription;
    Long categoryId;
    List<Long> deliveryMethodIds;
    List<Long> similarProductIds;
    List<UpdateProductPriceCommand> updateProductPriceCommands;
    List<UpdateProductCharacteristicCommand> updateProductCharacteristicCommands;
    List<UpdateProductFaqCommand> updateProductFaqCommands;
    List<UpdateProductDeliveryMethodDetailsCommand> updateProductDeliveryMethodDetailsCommands;
    List<UpdateProductPackageOptionCommand> updateProductPackageOptionCommands;
    List<UpdateProductMediaAltTextCommand> updateProductMediaAltTextCommands;
    Integer minimumOrderQuantity;
    ZonedDateTime discountExpirationDate;
    List<UpdateOldMediaDto> oldProductMedia;
    List<UpdateOldMediaDto> oldVendorDetailsMedia;
    List<MultipartFile> productMedia;
    List<MultipartFile> productVendorDetailsMedia;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed product updating");
            return Success.INSTANCE;
        }

        static Result productNotFound(Long productId) {
            log.info("Product with ID '{}' not found", productId);
            return ProductNotFound.of(productId);
        }

        static Result invalidOwner(Long productId, UserLogin userLogin) {
            log.info("Invalid owner with login '{}' for product with ID '{}'", userLogin, productId);
            return InvalidOwner.of(productId, userLogin);
        }

        static Result errorSavingFiles(Exception e) {
            log.warn("Error saving product files: {}", e.getMessage());
            return ErrorSavingFiles.INSTANCE;
        }

        static Result errorDeletingFiles(Exception e) {
            log.warn("Error deleting product files: {}", e.getMessage());
            return ErrorDeletingFiles.INSTANCE;
        }

        static Result errorSavingProduct(Exception e) {
            log.warn("Error saving product: {}", e.getMessage());
            return ErrorSavingProduct.INSTANCE;
        }

        static Result categoryNotFound(Long categoryId) {
            log.warn("Category with ID '{}' not found", categoryId);
            return CategoryNotFound.of(categoryId);
        }

        static Result deliveryMethodNotFound(Long deliveryMethodId) {
            log.warn("Delivery Method with ID '{}' not found", deliveryMethodId);
            return DeliveryMethodNotFound.of(deliveryMethodId);
        }

        static Result emptyFile() {
            log.warn("Empty file");
            return EmptyFile.INSTANCE;
        }

        static Result invalidMediaType(String mediaType) {
            log.warn("Invalid media type '{}'", mediaType);
            return InvalidMediaType.of(mediaType);
        }

        static Result similarProductNotFound(Long similarProductId) {
            log.warn("Similar product with ID '{}' not found", similarProductId);
            return SimilarProductNotFound.of(similarProductId);
        }

        static Result emptyTranslations(String moduleName) {
            log.warn("Empty translations in module: {}", moduleName);
            return EmptyTranslations.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.warn("Translation error: {}", e.getMessage());
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
        class ProductNotFound implements Result {
            Long productId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processProductNotFound(this);
            }
        }

        @Value(staticConstructor = "of")
        class InvalidOwner implements Result {
            Long productId;
            UserLogin userLogin;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidOwner(this);
            }
        }

        enum ErrorSavingFiles implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processErrorSavingFiles(this);
            }
        }

        enum ErrorSavingProduct implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processErrorSavingProduct(this);
            }
        }

        enum ErrorDeletingFiles implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processErrorDeletingFiles(this);
            }
        }

        @Value(staticConstructor = "of")
        class CategoryNotFound implements Result {
            Long categoryId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processCategoryNotFound(this);
            }
        }

        @Value(staticConstructor = "of")
        class DeliveryMethodNotFound implements Result {
            Long deliveryMethodId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeliveryMethodNotFound(this);
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
        class InvalidMediaType implements Result {
            String mediaType;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidMediaType(this);
            }
        }

        @Value(staticConstructor = "of")
        class SimilarProductNotFound implements Result {
            Long productId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSimilarProductNotFound(this);
            }
        }

        enum EmptyTranslations implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyTranslations(this);
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
            T processProductNotFound(ProductNotFound result);
            T processInvalidOwner(InvalidOwner result);
            T processErrorSavingFiles(ErrorSavingFiles result);
            T processErrorSavingProduct(ErrorSavingProduct result);
            T processErrorDeletingFiles(ErrorDeletingFiles result);
            T processCategoryNotFound(CategoryNotFound result);
            T processDeliveryMethodNotFound(DeliveryMethodNotFound result);
            T processEmptyFile(EmptyFile result);
            T processInvalidMediaType(InvalidMediaType result);
            T processSimilarProductNotFound(SimilarProductNotFound result);
            T processEmptyTranslations(EmptyTranslations result);
            T processTranslationError(TranslationError result);
        }
    }
}
