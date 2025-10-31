package com.surofu.exporteru.infrastructure.web.mapper.product;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.product.operation.GetProductSummaryViewPageByVendorId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProductSummaryViewPageByVendorIdMapperResultToResponseEntity
        implements GetProductSummaryViewPageByVendorId.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetProductSummaryViewPageByVendorId.Result.Success result) {
        return new ResponseEntity<>(result.getProductSummaryViewDtoPage(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processVendorNotFound(GetProductSummaryViewPageByVendorId.Result.VendorNotFound result) {
        String message = localizationManager.localize("vendor.not_found_by_id", result.getVendorId());
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.NOT_FOUND);
    }
}
