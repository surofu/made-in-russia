package com.surofu.exporteru.infrastructure.persistence.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.exporteru.application.exception.EmptyTranslationException;
import com.surofu.exporteru.core.repository.TranslationRepository;
import java.io.IOException;
import java.util.HashMap;
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
  public TranslationResponse translateToEn(String... texts) throws IOException {
    return translate("en", texts);
  }

  @Override
  public TranslationResponse translateToRu(String... texts) throws IOException {
    return translate("ru", texts);
  }

  @Override
  public TranslationResponse translateToZh(String... texts) throws IOException {
    return translate("zh", texts);
  }

  @Override
  public TranslationResponse translateToIn(String... texts) throws IOException {
    return translate("hi", texts);
  }

  public Map<String, String> expand(Map<String, String> translations)
      throws EmptyTranslationException, IOException {
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
      String translated = translateToIn(primary).getTranslations()[0].getText();
      result.put("hi", translated);
    }

    return result;
  }

  @Override
  public Map<String, String> expand(String text) throws EmptyTranslationException, IOException {
    String en = translateToEn(text).getTranslations()[0].getText();
    String ru = translateToRu(text).getTranslations()[0].getText();
    String zh = translateToZh(text).getTranslations()[0].getText();
    String hi = translateToIn(text).getTranslations()[0].getText();
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

  private TranslationResponse translate(String language, String... texts) throws IOException {
    if (texts == null || texts.length == 0) {
      return new YandexTranslationResponse(new YandexTranslation[] {});
    }

    TranslationRequest translationRequest = new TranslationRequest(
        language,
        texts,
        folderId
    );

    String requestBody = objectMapper.writeValueAsString(translationRequest);

    return yandexTranslatorRestClient.post()
        .header("Content-Type", "application/json")
        .header("Authorization", String.format("Api-Key %s", apiSecret))
        .body(requestBody)
        .retrieve()
        .body(YandexTranslationResponse.class);
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
