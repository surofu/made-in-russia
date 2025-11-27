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
    return translate("hi", texts);
  }

  public Map<String, String> expand(Map<String, String> translations) {
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
    if (texts == null || texts.length == 0) {
      return new YandexTranslationResponse(new YandexTranslation[] {});
    }

    TranslationRequest translationRequest = new TranslationRequest(
        language,
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
    private final String[] texts;
    private final String folderId;
    private final String textType;

    private TranslationRequest(
        String targetLanguageCode,
        String[] texts,
        String folderId
    ) {
      this.targetLanguageCode = targetLanguageCode;
      this.texts = texts;
      this.folderId = folderId;
      this.textType = "html";
    }
  }
}
