package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductSummaryViewDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductSummaryViewPageByVendorId {
    Locale locale;
    Long vendorId;
    int page;
    int size;
    String title;
    List<Long> deliveryMethodIds;
    List<Long> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Page<ProductSummaryViewDto> productSummaryViewDtoPage) {
            log.info("Successfully retrieved product summary view page by vendor id with total elements: {}", productSummaryViewDtoPage.getTotalElements());
            return Success.of(productSummaryViewDtoPage);
        }

        static Result vendorNotFound(Long vendorId) {
            log.warn("Error when getting product summary page by vendor id, vendor with ID '{}' not found", vendorId);
            return VendorNotFound.of(vendorId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Page<ProductSummaryViewDto> productSummaryViewDtoPage;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class VendorNotFound implements Result {
            Long vendorId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processVendorNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processVendorNotFound(VendorNotFound result);
        }
    }
}
