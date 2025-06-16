package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPageByVendorId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductSummaryViewPageByVendorIdMapperResultToResponseEntity
implements GetProductSummaryViewPageByVendorId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductSummaryViewPageByVendorId.Result.Success result) {
        return new ResponseEntity<>(result.getProductSummaryViewDtoPage(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processVendorNotFound(GetProductSummaryViewPageByVendorId.Result.VendorNotFound result) {
        String message = String.format("Продавец с ID '%s' не найден", result.getVendorId());
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.NOT_FOUND);
    }
}
