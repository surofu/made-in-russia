package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.WebLocalizationDto;
import com.surofu.madeinrussia.application.service.async.AsyncWebLocalizationService;
import com.surofu.madeinrussia.core.model.localization.WebLocalization;
import com.surofu.madeinrussia.core.repository.WebLocalizationRepository;
import com.surofu.madeinrussia.core.service.localization.LocalizationService;
import com.surofu.madeinrussia.core.service.localization.service.DeleteLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.GetAllLocalizations;
import com.surofu.madeinrussia.core.service.localization.service.GetLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.SaveLocalizationByLanguageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebLocalizationApplicationService implements LocalizationService {

    private final WebLocalizationRepository repository;
    private final AsyncWebLocalizationService asyncService;

    @Override
    public GetAllLocalizations.Result getAllLocalizations() {
        List<WebLocalization> localization = repository.getAll();
        Map<String, Object> content = localization.stream()
                .collect(Collectors.toMap(
                        WebLocalization::getLanguageCode,
                        WebLocalization::getContent
                ));
        return GetAllLocalizations.Result.success(new WebLocalizationDto(content));
    }

    @Override
    public GetLocalizationByLanguageCode.Result getLocalizationByLanguageCode(GetLocalizationByLanguageCode operation) {
        Optional<WebLocalization> localization =  repository.getByLanguageCode(operation.getLanguageCode());

        if (localization.isEmpty()) {
            return GetLocalizationByLanguageCode.Result.notFound(operation.getLanguageCode());
        }

        return GetLocalizationByLanguageCode.Result.success(localization.get().getContent());
    }

    @Override
    public SaveLocalizationByLanguageCode.Result saveLocalization(SaveLocalizationByLanguageCode operation) {
        WebLocalization localization = repository.getByLanguageCode(operation.getLanguageCode())
                .orElse(new WebLocalization());
        localization.setLanguageCode(operation.getLanguageCode());
        localization.setContent(operation.getContent());
        asyncService.save(localization);
        return SaveLocalizationByLanguageCode.Result.success();
    }

    @Override
    public DeleteLocalizationByLanguageCode.Result deleteLocalization(DeleteLocalizationByLanguageCode operation) {
        Optional<WebLocalization> localization = repository.getByLanguageCode(operation.getLanguageCode());

        if (localization.isPresent()) {
            asyncService.delete(localization.get());
            return DeleteLocalizationByLanguageCode.Result.success();
        }

        return DeleteLocalizationByLanguageCode.Result.notFound(operation.getLanguageCode());
    }
}
