package com.surofu.exporteru.core.service.deliveryTerm.operation;

import com.surofu.exporteru.application.dto.deliveryTerm.DeliveryTermDto;
import java.util.List;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetAllDeliveryTerms {

  public interface Result {
    <T> T process(Processor<T> processor);

    static Result success(List<DeliveryTermDto> dtos) {
      log.info("Successfully get all delivery-terms: {}", dtos.size());
      return Success.of(dtos);
    }

    @Value(staticConstructor = "of")
    class Success implements Result {
      List<DeliveryTermDto> dtos;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processSuccess(this);
      }
    }

    interface Processor<T> {
      T processSuccess(Success result);
    }
  }
}
