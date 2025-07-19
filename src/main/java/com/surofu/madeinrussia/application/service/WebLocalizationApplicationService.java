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
        List<WebLocalization> webLocalizations = repository.getAll();
        Map<String, Object> content = webLocalizations.stream()
                .collect(Collectors.toMap(
                        WebLocalization::getLanguageCode,
                        WebLocalization::getContent
                ));
        return GetAllLocalizations.Result.success(new WebLocalizationDto(content));
    }

    @Override
    public GetLocalizationByLanguageCode.Result getLocalizationByLanguageCode(GetLocalizationByLanguageCode operation) {
        return repository.getByLanguageCode(operation.getLanguageCode())
                .map(l -> GetLocalizationByLanguageCode.Result.success(l.getContent()))
                .orElse(GetLocalizationByLanguageCode.Result.notFound(operation.getLanguageCode()));
    }

    @Override
    public SaveLocalizationByLanguageCode.Result saveLocalization(SaveLocalizationByLanguageCode operation) {
        WebLocalization webLocalization = repository.getByLanguageCode(operation.getLanguageCode())
                .orElse(new WebLocalization());
        webLocalization.setLanguageCode(operation.getLanguageCode());
        webLocalization.setContent(operation.getContent());
        asyncService.save(webLocalization);
        return SaveLocalizationByLanguageCode.Result.success();
    }

    @Override
    public DeleteLocalizationByLanguageCode.Result deleteLocalization(DeleteLocalizationByLanguageCode operation) {
        Optional<WebLocalization> webLocalization = repository.getByLanguageCode(operation.getLanguageCode());

        if (webLocalization.isPresent()) {
            asyncService.delete(webLocalization.get());
            return DeleteLocalizationByLanguageCode.Result.success();
        }

        return DeleteLocalizationByLanguageCode.Result.notFound(operation.getLanguageCode());
    }
}
