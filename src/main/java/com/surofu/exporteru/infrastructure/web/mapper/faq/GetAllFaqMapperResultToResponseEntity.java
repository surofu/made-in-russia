package com.surofu.exporteru.infrastructure.web.mapper.faq;

import com.surofu.exporteru.core.service.faq.operation.GetAllFaq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAllFaqMapperResultToResponseEntity
        implements GetAllFaq.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetAllFaq.Result.Success result) {
        return new ResponseEntity<>(result.getFaqDtos(), HttpStatus.OK);
    }
}
