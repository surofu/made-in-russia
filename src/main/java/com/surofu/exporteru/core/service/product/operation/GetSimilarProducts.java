package com.surofu.exporteru.core.service.product.operation;

import com.surofu.exporteru.application.dto.product.ProductSummaryViewDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import java.util.List;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetSimilarProducts {
  Long productId;
  SecurityUser securityUser;

  public interface Result {
    <T> T process(Processor<T> processor);

    static Result success(List<ProductSummaryViewDto> products) {
      log.info("Successfully processed similar products: {}", products.size());
      return Success.of(products);
    }

    static Result notFound(Long productId) {
      log.warn("Product with ID \"{}\" not found", productId);
      return NotFound.of(productId);
    }

    @Value(staticConstructor = "of")
    class Success implements Result {
      List<ProductSummaryViewDto> products;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processSuccess(this);
      }
    }

    @Value(staticConstructor = "of")
    class NotFound implements Result {
      Long productId;

      @Override
      public <T> T process(Processor<T> processor) {
        return processor.processNotFound(this);
      }
    }

    interface Processor<T> {
      T processSuccess(Success result);

      T processNotFound(NotFound result);
    }
  }
}
