package com.surofu.exporteru.infrastructure.persistence.translation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.exporteru.application.exception.EmptyTranslationException;
import com.surofu.exporteru.core.repository.TranslationRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexTranslationRepository implements TranslationRepository {

  private final RestClient yandexTranslatorRestClient;
  private final ObjectMapper objectMapper;

  @Value("${app.yandex.translate.folder-id}")
  private String folderId;
  @Value("${app.yandex.translate.secret}")
  private String apiSecret;

  @Override
  public TranslationResponse translateToEn(String... texts) {
    log.info("translateToEn: {} texts", texts.length);
    return translate("en", texts);
  }

  @Override
  public TranslationResponse translateToRu(String... texts) {
    log.info("translateToRu: {} texts", texts.length);
    return translate("ru", texts);
  }

  @Override
  public TranslationResponse translateToZh(String... texts) {
    log.info("translateToZh: {} texts", texts.length);
    return translate("zh", texts);
  }

  @Override
  public TranslationResponse translateToHi(String... texts) {
    log.info("translateToHi: {} texts", texts.length);
    return translateInternal("hi", null, texts);
  }

  @Override
  public TranslationResponse translate(String targetLanguage, String sourceLanguage, String... texts) {
    log.info("translate: targetLanguage={}, sourceLanguage={}, {} texts", targetLanguage, sourceLanguage, texts.length);
    String effectiveSourceLanguage = sourceLanguage;
    if (StringUtils.isBlank(sourceLanguage) && texts.length > 0) {
      effectiveSourceLanguage = detectLanguageByUnicode(texts[0]);
      if (effectiveSourceLanguage != null) {
        log.info("Detected source language by Unicode: {}", effectiveSourceLanguage);
      }
    }

    return translateInternal(targetLanguage, effectiveSourceLanguage, texts);
  }

  /**
   * Detect language by Unicode character ranges.
   * Returns language code if detected, null otherwise.
   */
  private String detectLanguageByUnicode(String text) {
    if (text == null || text.isEmpty()) {
      return null;
    }

    int hindiCount = 0;
    int chineseCount = 0;
    int cyrillicCount = 0;
    int latinCount = 0;
    int totalLetters = 0;

    for (char c : text.toCharArray()) {
      if (Character.isLetter(c)) {
        totalLetters++;
        if (c >= 0x0900 && c <= 0x097F) {
          hindiCount++;
        }
        else if ((c >= 0x4E00 && c <= 0x9FFF) || (c >= 0x3400 && c <= 0x4DBF)) {
          chineseCount++;
        }
        else if (c >= 0x0400 && c <= 0x04FF) {
          cyrillicCount++;
        }
        else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
          latinCount++;
        }
      }
    }

    if (totalLetters == 0) {
      return null;
    }

    double threshold = 0.3;

    if ((double) hindiCount / totalLetters > threshold) {
      return "hi";
    }
    if ((double) chineseCount / totalLetters > threshold) {
      return "zh";
    }
    if ((double) cyrillicCount / totalLetters > threshold) {
      return "ru";
    }
    if ((double) latinCount / totalLetters > threshold) {
      return "en";
    }

    return null;
  }

  public Map<String, String> expand(Map<String, String> translations) {
    if (translations == null || translations.isEmpty()) {
      return new HashMap<>();
    }

    String en = StringUtils.trimToNull(translations.get("en"));
    String ru = StringUtils.trimToNull(translations.get("ru"));
    String zh = StringUtils.trimToNull(translations.get("zh"));
    String hi = StringUtils.trimToNull(translations.get("hi"));

    if (en == null && ru == null && zh == null && hi == null) {
      return new HashMap<>();
    }

    String primary = getPrimaryLocaleText(translations);

    Map<String, String> result = new HashMap<>();
    result.put("en", en);
    result.put("ru", ru);
    result.put("zh", zh);
    result.put("hi", hi);

    if (en == null) {
      String translated = translateToEn(primary).getTranslations()[0].getText();
      result.put("en", translated);
    }

    if (ru == null) {
      String translated = translateToRu(primary).getTranslations()[0].getText();
      result.put("ru", translated);
    }

    if (zh == null) {
      String translated = translateToZh(primary).getTranslations()[0].getText();
      result.put("zh", translated);
    }

    if (hi == null) {
      String translated = translateToHi(primary).getTranslations()[0].getText();
      result.put("hi", translated);
    }

    return result;
  }

  @Override
  public Map<String, String> expand(String text) {
    if (StringUtils.trimToNull(text) == null) {
      return new HashMap<>();
    }

    String en = translateToEn(text).getTranslations()[0].getText();
    String ru = translateToRu(text).getTranslations()[0].getText();
    String zh = translateToZh(text).getTranslations()[0].getText();
    String hi = translateToHi(text).getTranslations()[0].getText();
    Map<String, String> result = new HashMap<>();
    result.put("en", en);
    result.put("ru", ru);
    result.put("zh", zh);
    result.put("hi", hi);
    return result;
  }

  private String getPrimaryLocaleText(Map<String, String> translations) {
    String en = StringUtils.trimToNull(translations.get("en"));
    String ru = StringUtils.trimToNull(translations.get("ru"));
    String zh = StringUtils.trimToNull(translations.get("zh"));
    String hi = StringUtils.trimToNull(translations.get("hi"));

    if (ru != null) {
      return ru;
    }
    if (en != null) {
      return en;
    }
    if (zh != null) {
      return zh;
    }
    if (hi != null) {
      return hi;
    }

    throw new EmptyTranslationException();
  }

  private TranslationResponse translate(String language, String... texts) {
    return translateInternal(language, null, texts);
  }

  private TranslationResponse translateInternal(String targetLanguage, String sourceLanguage, String... texts) {
    if (texts == null || texts.length == 0) {
      return new YandexTranslationResponse(new YandexTranslation[] {});
    }

    TranslationRequest translationRequest = new TranslationRequest(
        targetLanguage,
        sourceLanguage,
        texts,
        folderId
    );

    String requestBody;
    try {
      requestBody = objectMapper.writeValueAsString(translationRequest);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return yandexTranslatorRestClient.post()
        .header("Content-Type", "application/json")
        .header("Authorization", String.format("Api-Key %s", apiSecret))
        .body(requestBody)
        .retrieve()
        .body(YandexTranslationResponse.class);
  }

  @Override
  public List<Map<String, String>> expand(List<String> texts) {
    if (texts.isEmpty()) {
      return new ArrayList<>();
    }

    Translation[] enTranslations = translateToEn(texts.toArray(String[]::new)).getTranslations();
    Translation[] ruTranslations = translateToRu(texts.toArray(String[]::new)).getTranslations();
    Translation[] zhTranslations = translateToZh(texts.toArray(String[]::new)).getTranslations();
    Translation[] hiTranslations = translateToHi(texts.toArray(String[]::new)).getTranslations();

    List<Map<String, String>> result = new ArrayList<>();

    for (int i = 0; i < texts.size(); i++) {
      Map<String, String> translation = new HashMap<>();
      translation.put("en", enTranslations[i].getText());
      translation.put("ru", ruTranslations[i].getText());
      translation.put("zh", zhTranslations[i].getText());
      translation.put("hi", hiTranslations[i].getText());
      result.add(translation);
    }

    return result;
  }

  @Override
  public Map<String, Map<String, String>> expandMap(Map<String, String> texts) {
    if (texts == null || texts.isEmpty()) {
      return new HashMap<>();
    }

    List<String> keys = new ArrayList<>(texts.size());
    List<String> values = new ArrayList<>(texts.size());

    texts.forEach((key, value) -> {
      keys.add(key);
      values.add(value);
    });

    Translation[] enTranslations = translateToEn(values.toArray(String[]::new)).getTranslations();
    Translation[] ruTranslations = translateToRu(values.toArray(String[]::new)).getTranslations();
    Translation[] zhTranslations = translateToZh(values.toArray(String[]::new)).getTranslations();
    Translation[] hiTranslations = translateToHi(values.toArray(String[]::new)).getTranslations();

    Map<String, Map<String, String>> result = new HashMap<>();
    for (int i = 0; i < keys.size(); i++) {
      String key = keys.get(i);
      Map<String, String> translation = new HashMap<>();
      translation.put("en", enTranslations[i].getText());
      translation.put("ru", ruTranslations[i].getText());
      translation.put("zh", zhTranslations[i].getText());
      translation.put("hi", hiTranslations[i].getText());
      result.put(key, translation);
    }

    return result;
  }

  @Data
  private static final class TranslationRequest {
    private final String targetLanguageCode;
    private final String sourceLanguageCode;
    private final String[] texts;
    private final String folderId;
    private final String textType;

    private TranslationRequest(
        String targetLanguageCode,
        String sourceLanguageCode,
        String[] texts,
        String folderId
    ) {
      this.targetLanguageCode = targetLanguageCode;
      this.sourceLanguageCode = sourceLanguageCode;
      this.texts = texts;
      this.folderId = folderId;
      this.textType = "html";
    }
  }
}
