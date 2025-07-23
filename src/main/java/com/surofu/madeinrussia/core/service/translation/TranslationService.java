package com.surofu.madeinrussia.core.service.translation;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;

import java.util.Map;

public interface TranslationService {
    TranslationResponse translateToEn(String... strings);

    TranslationResponse translateToRu(String... strings);

    TranslationResponse translateToZh(String... strings);

    Map<String, TranslationResponse> translateToEn(Map<String, String[]> map);

    Map<String, TranslationResponse> translateToRu(Map<String, String[]> map);

    Map<String, TranslationResponse> translateToZh(Map<String, String[]> map);

    TranslationResponse mergeTranslations(TranslationDto dto1, TranslationDto dto2);

    Map<String, TranslationResponse> mergeTranslations(Map<String, TranslationDto[]> map1, Map<String, TranslationDto[]> map2);
}
