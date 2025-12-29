package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.infrastructure.persistence.translation.TranslationResponse;
import java.util.List;
import java.util.Map;

public interface TranslationRepository {
  TranslationResponse translateToEn(String... texts);

  TranslationResponse translateToRu(String... texts);

  TranslationResponse translateToZh(String... texts);

  TranslationResponse translateToHi(String... texts);

 
  TranslationResponse translate(String targetLanguage, String sourceLanguage, String... texts);

  Map<String, String> expand(Map<String, String> translations);

  Map<String, String> expand(String text);

  List<Map<String, String>> expand(List<String> text);

  Map<String, Map<String, String>> expandMap(Map<String, String> text);
}
