package com.surofu.exporteru.core.service.order.operation;

import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class CreateOrder {
  Long productId;
  UserLogin login;
  Integer quantity;
  String comment;

  public interface Result {
    static Result success(Long productId, UserLogin login) {
      log.info("Successfully created a new order for product {} with login {}", productId, login);
      return Success.INSTANCE;
    }

    static Result notFound(Long productId) {
      log.warn("The product with id {} does not exist.", productId);
      return NotFound.of(productId);
    }

    <T> T process(Processor<T> processor);

    enum Success implements Result {
      INSTANCE;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processSuccess(this);
      }
    }

    interface Processor<T> {
      T processSuccess(Success result);

      T processProductNotFound(NotFound result);
    }

    @Value(staticConstructor = "of")
    class NotFound implements Result {
      Long productId;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processProductNotFound(this);
      }
    }
  }
}
