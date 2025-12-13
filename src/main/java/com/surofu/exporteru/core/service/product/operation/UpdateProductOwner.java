package com.surofu.exporteru.core.service.product.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateProductOwner {
  Long productId;
  Long ownerId;

  public interface Result {
    <T> T process(Processor<T> processor);

    static Result success(Long productId, Long ownerId) {
      log.info("Successfully updated product ({}) owner ({})", productId, ownerId);
      return Success.INSTANCE;
    }

    static Result productNotFound(Long productId) {
      log.warn("Product with ID \"{}\" not found", productId);
      return ProductNotFound.of(productId);
    }

    static Result userNotFound(Long userId) {
      log.warn("User with ID \"{}\" not found", userId);
      return UserNotFound.of(userId);
    }

    static Result userNotVendor(Long userId) {
      log.warn("User with ID \"{}\" not vendor", userId);
      return UserNotVendor.of(userId);
    }

    static Result saveError(Long productId, Long userId, Exception e) {
      log.error("Error updating product ({}) owner ({}): {}", productId, userId, e.getMessage(), e);
      return SaveError.INSTANCE;
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
    class UserNotFound implements Result {
      Long userId;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processUserNotFound(this);
      }
    }

    @Value(staticConstructor = "of")
    class UserNotVendor implements Result {
      Long userId;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processUserNotVendor(this);
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

      T processUserNotFound(UserNotFound result);

      T processUserNotVendor(UserNotVendor result);

      T processSaveError(SaveError result);
    }
  }
}
