package com.surofu.exporteru.core.service.deliveryTerm.operation;

import com.surofu.exporteru.application.dto.deliveryTerm.DeliveryTermDto;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermCode;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermDescription;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class SaveDeliveryTerm {
  Long id;
  DeliveryTermCode code;
  DeliveryTermName name;
  DeliveryTermDescription description;

  public interface Result {
    <T> T process(Processor<T> processor);

    static Result success(DeliveryTermDto dto) {
      log.info("Successfully saved delivery-term with code {}", dto.getCode());
      return Success.INSTANCE;
    }

    static Result notFound(Long id) {
      log.warn("No delivery-term with id {}", id);
      return NotFound.of(id);
    }

    static Result alreadyExistWithCode(DeliveryTermCode code) {
      log.warn("Delivery term with code {} already exist", code);
      return AlreadyExistWithCode.of(code);
    }

    static Result saveError(Exception e) {
      log.error("Error while saving delivery-term: {}", e.getMessage(), e);
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
    class NotFound implements Result {
      Long id;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processNotFound(this);
      }
    }

    @Value(staticConstructor = "of")
    class AlreadyExistWithCode implements Result {
      DeliveryTermCode code;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processAlreadyExistWithCode(this);
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

      T processNotFound(NotFound result);

      T processAlreadyExistWithCode(AlreadyExistWithCode result);

      T processSaveError(SaveError result);
    }
  }
}
