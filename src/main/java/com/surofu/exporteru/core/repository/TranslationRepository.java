package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.application.exception.EmptyTranslationException;
import com.surofu.exporteru.infrastructure.persistence.translation.TranslationResponse;
import java.io.IOException;
import java.util.Map;

public interface TranslationRepository {
  TranslationResponse translateToEn(String... texts) throws IOException;

  TranslationResponse translateToRu(String... texts) throws IOException;

  TranslationResponse translateToZh(String... texts) throws IOException;

  TranslationResponse translateToIn(String... texts) throws IOException;

  Map<String, String> expand(Map<String, String> translations);

  Map<String, String> expand(String text);
}
