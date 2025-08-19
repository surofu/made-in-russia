package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.cache.WebLocalizationCacheManager;
import com.surofu.madeinrussia.application.dto.WebLocalizationDto;
import com.surofu.madeinrussia.core.model.localization.WebLocalization;
import com.surofu.madeinrussia.core.repository.WebLocalizationRepository;
import com.surofu.madeinrussia.core.service.localization.LocalizationService;
import com.surofu.madeinrussia.core.service.localization.service.DeleteLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.GetAllLocalizations;
import com.surofu.madeinrussia.core.service.localization.service.GetLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.SaveLocalizationByLanguageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebLocalizationApplicationService implements LocalizationService, ApplicationRunner {

    private final WebLocalizationRepository repository;
    private final WebLocalizationCacheManager cacheManager;

    @Override
    public GetAllLocalizations.Result getAllLocalizations() {
        Map<String, WebLocalizationDto> cachedAll = cacheManager.getAll();

        if (cachedAll != null && !cachedAll.isEmpty()) {
            Map<String, Object> content = cachedAll.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return GetAllLocalizations.Result.success(new WebLocalizationDto(content));
        }

        List<WebLocalization> localization = repository.getAll();
        Map<String, Object> content = localization.stream()
                .collect(Collectors.toMap(
                        WebLocalization::getLanguageCode,
                        WebLocalization::getContent
                ));

        localization.forEach(wl -> cacheManager.setWebLocalization(wl.getLanguageCode(), new WebLocalizationDto(wl.getContent())));
        return GetAllLocalizations.Result.success(new WebLocalizationDto(content));
    }

    @Override
    public GetLocalizationByLanguageCode.Result getLocalizationByLanguageCode(GetLocalizationByLanguageCode operation) {
        WebLocalizationDto cached = cacheManager.getWebLocalization(operation.getLanguageCode());

        if (cached != null) {
            return GetLocalizationByLanguageCode.Result.success(cached.content());
        }

        Optional<WebLocalization> localization =  repository.getByLanguageCode(operation.getLanguageCode());

        if (localization.isEmpty()) {
            return GetLocalizationByLanguageCode.Result.notFound(operation.getLanguageCode());
        }

        cacheManager.setWebLocalization(operation.getLanguageCode(), new WebLocalizationDto(localization.get().getContent()));
        return GetLocalizationByLanguageCode.Result.success(localization.get().getContent());
    }

    @Override
    public SaveLocalizationByLanguageCode.Result saveLocalization(SaveLocalizationByLanguageCode operation) {
        WebLocalization localization = repository.getByLanguageCode(operation.getLanguageCode())
                .orElse(new WebLocalization());
        localization.setLanguageCode(operation.getLanguageCode());
        localization.setContent(operation.getContent());

        try {
            repository.save(localization);
        } catch (Exception e) {
            return SaveLocalizationByLanguageCode.Result.saveError(e);
        }

        cacheManager.setWebLocalization(operation.getLanguageCode(), new WebLocalizationDto(operation.getContent()));
        return SaveLocalizationByLanguageCode.Result.success();
    }

    @Override
    public DeleteLocalizationByLanguageCode.Result deleteLocalization(DeleteLocalizationByLanguageCode operation) {
        Optional<WebLocalization> localization = repository.getByLanguageCode(operation.getLanguageCode());

        if (localization.isEmpty()) {
            return DeleteLocalizationByLanguageCode.Result.notFound(operation.getLanguageCode());

        }

        try {
            repository.delete(localization.get());
        } catch (Exception e) {
            return DeleteLocalizationByLanguageCode.Result.deleteError(e);
        }

        cacheManager.removeWebLocalization(operation.getLanguageCode());
        return DeleteLocalizationByLanguageCode.Result.success();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        cacheManager.clearAll();

        log.info("Initializing all web localization");
        getAllLocalizations();
        log.info("Initializing web localization: en");
        getLocalizationByLanguageCode(GetLocalizationByLanguageCode.of("en"));
        log.info("Initializing web localization: ru");
        getLocalizationByLanguageCode(GetLocalizationByLanguageCode.of("ru"));
        log.info("Initializing web localization: zh");
        getLocalizationByLanguageCode(GetLocalizationByLanguageCode.of("zh"));
    }
}
