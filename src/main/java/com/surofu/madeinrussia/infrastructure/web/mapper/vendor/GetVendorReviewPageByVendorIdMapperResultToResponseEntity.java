package com.surofu.madeinrussia.infrastructure.web.mapper.vendor;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetVendorReviewPageByVendorIdMapperResultToResponseEntity
implements GetVendorReviewPageById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetVendorReviewPageById.Result.Success result) {
        return new ResponseEntity<>(result.getVendorReviewPageDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processVendorNotFound(GetVendorReviewPageById.Result.VendorNotFount result) {
        String message = String.format("Продавец с ID '%s' не найден", result.getVendorId());
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.NOT_FOUND);
    }
}
