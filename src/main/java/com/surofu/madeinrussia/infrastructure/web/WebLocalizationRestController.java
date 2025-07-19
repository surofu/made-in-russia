package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.core.service.localization.LocalizationService;
import com.surofu.madeinrussia.core.service.localization.service.DeleteLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.GetAllLocalizations;
import com.surofu.madeinrussia.core.service.localization.service.GetLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.SaveLocalizationByLanguageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/localization")
public class WebLocalizationRestController {

    private final LocalizationService service;

    private final GetAllLocalizations.Result.Processor<ResponseEntity<?>> getAllLocalizationsProcessor;
    private final GetLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> getLocalizationByLanguageCodeProcessor;
    private final SaveLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> saveLocalizationByLanguageCodeProcessor;
    private final DeleteLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> deleteLocalizationByLanguageCodeProcessor;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllLocalizations() {
        return service.getAllLocalizations().process(getAllLocalizationsProcessor);
    }

    @GetMapping("{languageCode}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getLocalizationByLanguageCode(@PathVariable String languageCode) {
        GetLocalizationByLanguageCode operation = GetLocalizationByLanguageCode.of(languageCode);
        return service.getLocalizationByLanguageCode(operation).process(getLocalizationByLanguageCodeProcessor);
    }

    @PostMapping("{languageCode}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveLocalizationByLanguageCode(@PathVariable String languageCode, @RequestBody Map<String, Object> content) {
        SaveLocalizationByLanguageCode operation = SaveLocalizationByLanguageCode.of(languageCode, content);
        return service.saveLocalization(operation).process(saveLocalizationByLanguageCodeProcessor);
    }

    @DeleteMapping("{languageCode}")
    public ResponseEntity<?> deleteLocalizationByLanguageCode(@PathVariable String languageCode) {
        DeleteLocalizationByLanguageCode operation = DeleteLocalizationByLanguageCode.of(languageCode);
        return service.deleteLocalization(operation).process(deleteLocalizationByLanguageCodeProcessor);
    }
}
