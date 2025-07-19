package com.surofu.madeinrussia.infrastructure.web.mapper.localization;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.localization.service.SaveLocalizationByLanguageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveLocalizationByLanguageCodeMapperResultToResponseEntity
implements SaveLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(SaveLocalizationByLanguageCode.Result.Success result) {
        String message = localizationManager.localize("localization.save.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }
}
