package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.command.product.*;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.product.ProductDescription;
import com.surofu.madeinrussia.core.model.product.ProductTitle;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class CreateProduct {
    SecurityUser securityUser;
    ProductTitle productTitle;
    ProductDescription productDescription;
    Long categoryId;
    List<Long> deliveryMethodIds;
    List<CreateProductPriceCommand> createProductPriceCommands;
    List<CreateProductCharacteristicCommand> createProductCharacteristicCommands;
    List<CreateProductFaqCommand> createProductFaqCommands;
    List<CreateProductDeliveryMethodDetailsCommand> createProductDeliveryMethodDetailsCommands;
    List<CreateProductPackageOptionCommand> createProductPackageOptionCommands;
    Integer minimumOrderQuantity;
    ZonedDateTime discountExpirationDate;
    List<MultipartFile> files;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed product creation");
            return Success.INSTANCE;
        }

        static Result errorSavingFiles() {
            log.warn("Error saving product files");
            return ErrorSavingFiles.INSTANCE;
        }

        static Result errorSavingProduct() {
            log.warn("Error saving product");
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

        interface Processor<T> {
            T processSuccess(Success result);
            T processErrorSavingFiles(ErrorSavingFiles result);
            T processErrorSavingProduct(ErrorSavingProduct result);
            T processCategoryNotFound(CategoryNotFound result);
            T processDeliveryMethodNotFound(DeliveryMethodNotFound result);
        }
    }
}
