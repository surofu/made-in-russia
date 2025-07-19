package com.surofu.madeinrussia.infrastructure.web.mapper.localization;

import com.surofu.madeinrussia.core.service.localization.service.GetAllLocalizations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAllWebLocalizationMapperResultToResponseEntity
implements GetAllLocalizations.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetAllLocalizations.Result.Success result) {
        return new ResponseEntity<>(result.getWebLocalizationDto().content(), HttpStatus.OK);
    }
}
