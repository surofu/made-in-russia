package com.surofu.madeinrussia.infrastructure.web.mapper.user;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.user.operation.GetVendorById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetVendorByIdMapperResultToResponseEntity implements GetVendorById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetVendorById.Result.Success result) {
        return new ResponseEntity<>(result.getAbstractAccountDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetVendorById.Result.NotFound result) {
        String message = String.format("Продавец с ID '%s' не найден", result.getVendorId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
