package com.surofu.exporteru.infrastructure.web.mapper.general;

import com.surofu.exporteru.core.service.general.operation.GetAllGeneral;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAllGeneralMapperResultToResponseEntity
        implements GetAllGeneral.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetAllGeneral.Result.Success result) {
        return new ResponseEntity<>(result.getDto(), HttpStatus.OK);
    }
}
