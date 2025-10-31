package com.surofu.exporteru.core.service.vendor.operation;

import com.surofu.exporteru.application.dto.vendor.VendorReviewPageDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetVendorReviewPageById {
    Long vendorId;
    Integer page;
    Integer size;
    Integer minRating;
    Integer maxRating;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(VendorReviewPageDto vendorReviewPageDto) {
            log.info("Successfully processed get vendor reviews by vendor ID with total elements: {}", vendorReviewPageDto.getPage().getTotalElements());
            return Success.of(vendorReviewPageDto);
        }

        static Result vendorNotFound(Long vendorId) {
            log.warn("Vendor not found with ID: {}", vendorId);
            return VendorNotFount.of(vendorId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            VendorReviewPageDto vendorReviewPageDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class VendorNotFount implements Result {
            Long vendorId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processVendorNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processVendorNotFound(VendorNotFount result);
        }
    }
}
