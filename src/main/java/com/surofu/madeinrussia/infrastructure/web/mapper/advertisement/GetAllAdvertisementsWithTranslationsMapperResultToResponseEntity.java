package com.surofu.madeinrussia.infrastructure.web.mapper.advertisement;

import com.surofu.madeinrussia.core.service.advertisement.operation.GetAllAdvertisementsWithTranslations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAllAdvertisementsWithTranslationsMapperResultToResponseEntity
implements GetAllAdvertisementsWithTranslations.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetAllAdvertisementsWithTranslations.Result.Success result) {
        return new ResponseEntity<>(result.getAdvertisementDtoList(), HttpStatus.OK);
    }
}
