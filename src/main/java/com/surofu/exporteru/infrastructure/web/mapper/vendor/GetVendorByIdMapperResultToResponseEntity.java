package com.surofu.exporteru.infrastructure.web.mapper.vendor;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.vendor.operation.GetVendorById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetVendorByIdMapperResultToResponseEntity
        implements GetVendorById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetVendorById.Result.Success result) {
        return new ResponseEntity<>(result.getAbstractAccountDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetVendorById.Result.NotFound result) {
        String message = localizationManager.localize("vendor.not_found_by_id", result.getVendorId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
