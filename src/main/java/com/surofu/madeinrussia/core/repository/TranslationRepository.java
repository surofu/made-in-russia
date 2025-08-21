package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface TranslationRepository {
    TranslationResponse translateToEn(String ...texts) throws IOException;

    TranslationResponse translateToRu(String ...texts) throws IOException;

    TranslationResponse translateToZh(String ...texts) throws IOException;

    HstoreTranslationDto expand(HstoreTranslationDto dto) throws EmptyTranslationException, IOException;

    Map<String, List<HstoreTranslationDto>> expandStrings(Map<String, List<String>> map) throws IOException;

    Map<String, HstoreTranslationDto> expand(Map<String, HstoreTranslationDto> map) throws EmptyTranslationException, ExecutionException;
}
