package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.core.model.product.ProductArticleCode;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductByArticle {
    ProductArticleCode articleCode;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(ProductDto productDto) {
            log.info("Successfully processed get product by article: {}", productDto.getArticle());
            return Success.of(productDto);
        }

        static Result notFound(ProductArticleCode articleCode) {
            log.warn("Not found product by article: {}", articleCode.toString());
            return NotFound.of(articleCode);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            ProductDto productDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            ProductArticleCode articleCode;

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
