package com.surofu.madeinrussia.infrastructure.web.mapper.vendor;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetVendorReviewPageByVendorIdMapperResultToResponseEntity
        implements GetVendorReviewPageById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetVendorReviewPageById.Result.Success result) {
        return new ResponseEntity<>(result.getVendorReviewPageDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processVendorNotFound(GetVendorReviewPageById.Result.VendorNotFount result) {
        String message = localizationManager.localize("vendor.not_found_by_id", result.getVendorId());
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.NOT_FOUND);
    }
}
