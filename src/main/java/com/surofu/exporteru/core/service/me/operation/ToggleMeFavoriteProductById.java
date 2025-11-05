package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.dto.user.ToggleUserFavoriteProductStatusDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class ToggleMeFavoriteProductById {
    Long productId;
    SecurityUser securityUser;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(ToggleUserFavoriteProductStatusDto status) {
            log.info("Successfully processed toggle favorite product status: {}", status.isStatus());
            return Success.of(status);
        }

        static Result productNotFound(Long productId) {
            log.warn("While toggle user favorite product: product with ID \"{}\" not found", productId);
            return ProductNotFound.of(productId);
        }

        static Result saveError(Exception e, UserEmail userEmail, Long productId) {
            log.error("Error while processing toggle favorite product status: {}, User email: {}, Product ID: {}", e.getMessage(), userEmail.toString(), productId, e);
            return SaveError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            ToggleUserFavoriteProductStatusDto status;

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

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processProductNotFound(ProductNotFound result);

            T processSaveError(SaveError result);
        }
    }
}
