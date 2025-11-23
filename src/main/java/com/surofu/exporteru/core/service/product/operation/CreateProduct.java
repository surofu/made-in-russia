package com.surofu.exporteru.core.service.product.operation;

import com.surofu.exporteru.application.command.product.create.*;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.product.ProductDescription;
import com.surofu.exporteru.core.model.product.ProductDiscountExpirationDate;
import com.surofu.exporteru.core.model.product.ProductMinimumOrderQuantity;
import com.surofu.exporteru.core.model.product.ProductTitle;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class CreateProduct {
    SecurityUser securityUser;
    ProductTitle productTitle;
    ProductDescription productDescription;
    Long categoryId;
    List<Long> deliveryMethodIds;
    List<Long> similarProductIds;
    List<CreateProductPriceCommand> createProductPriceCommands;
    List<CreateProductCharacteristicCommand> createProductCharacteristicCommands;
    List<CreateProductFaqCommand> createProductFaqCommands;
    List<CreateProductDeliveryMethodDetailsCommand> createProductDeliveryMethodDetailsCommands;
    List<CreateProductPackageOptionCommand> createProductPackageOptionCommands;
    List<CreateProductMediaAltTextCommand> createProductMediaAltTextCommands;
    ProductMinimumOrderQuantity minimumOrderQuantity;
    ProductDiscountExpirationDate discountExpirationDate;
    List<MultipartFile> productMedia;
    List<MultipartFile> productVendorDetailsMedia;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed product creation");
            return Success.INSTANCE;
        }

        static Result errorSavingFiles(Exception e) {
            log.warn("Error saving product files: {}", e.getMessage());
            return ErrorSavingFiles.INSTANCE;
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
            log.warn("Empty translation: {}", moduleName);
            return EmptyTranslation.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.warn("Translation error", e);
            return TranslationError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
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

        enum EmptyTranslation implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyTranslation(this);
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
            T processErrorSavingFiles(ErrorSavingFiles result);
            T processErrorSavingProduct(ErrorSavingProduct result);
            T processCategoryNotFound(CategoryNotFound result);
            T processDeliveryMethodNotFound(DeliveryMethodNotFound result);
            T processEmptyFile(EmptyFile result);
            T processInvalidMediaType(InvalidMediaType result);
            T processSimilarProductNotFound(SimilarProductNotFound result);
            T processEmptyTranslation(EmptyTranslation result);
            T processTranslationError(TranslationError result);
        }
    }
}
