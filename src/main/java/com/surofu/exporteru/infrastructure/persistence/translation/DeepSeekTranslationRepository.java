package com.surofu.exporteru.infrastructure.persistence.translation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.exporteru.application.ai.DeepSeekService;
import com.surofu.exporteru.core.repository.TranslationRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekTranslationRepository implements TranslationRepository {
  private final DeepSeekService deepSeekService;
  private final ObjectMapper objectMapper;

  // ── public translate methods ────────────────────────────────────────────────

  @Override
  public TranslationResponse translateToEn(String... texts) {
    log.info("translateToEn: {} texts", texts.length);
    return translateInternal("en", null, texts);
  }

  @Override
  public TranslationResponse translateToRu(String... texts) {
    log.info("translateToRu: {} texts", texts.length);
    return translateInternal("ru", null, texts);
  }

  @Override
  public TranslationResponse translateToZh(String... texts) {
    log.info("translateToZh: {} texts", texts.length);
    return translateInternal("zh", null, texts);
  }

  @Override
  public TranslationResponse translateToHi(String... texts) {
    log.info("translateToHi: {} texts", texts.length);
    return translateInternal("hi", null, texts);
  }

  @Override
  public TranslationResponse translate(String targetLanguage, String sourceLanguage,
                                       String... texts) {
    log.info("translate: targetLanguage={}, sourceLanguage={}, {} texts",
        targetLanguage, sourceLanguage, texts.length);
    return translateInternal(targetLanguage, sourceLanguage, texts);
  }

  // ── expand helpers ──────────────────────────────────────────────────────────

  @Override
  public Map<String, String> expand(String text) {
    if (StringUtils.trimToNull(text) == null) {
      return new HashMap<>();
    }

    Map<String, String> result = new HashMap<>();
    result.put("en", translateToEn(text).getTranslations()[0].getText());
    result.put("ru", translateToRu(text).getTranslations()[0].getText());
    result.put("zh", translateToZh(text).getTranslations()[0].getText());
    result.put("hi", translateToHi(text).getTranslations()[0].getText());
    return result;
  }

  @Override
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
    result.put("en", en != null ? en : translateToEn(primary).getTranslations()[0].getText());
    result.put("ru", ru != null ? ru : translateToRu(primary).getTranslations()[0].getText());
    result.put("zh", zh != null ? zh : translateToZh(primary).getTranslations()[0].getText());
    result.put("hi", hi != null ? hi : translateToHi(primary).getTranslations()[0].getText());
    return result;
  }

  @Override
  public List<Map<String, String>> expand(List<String> texts) {
    if (texts == null || texts.isEmpty()) {
      return new ArrayList<>();
    }

    String[] arr = texts.toArray(String[]::new);
    Translation[] enTranslations = translateToEn(arr).getTranslations();
    Translation[] ruTranslations = translateToRu(arr).getTranslations();
    Translation[] zhTranslations = translateToZh(arr).getTranslations();
    Translation[] hiTranslations = translateToHi(arr).getTranslations();

    List<Map<String, String>> result = new ArrayList<>(texts.size());
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

    List<String> keys = new ArrayList<>(texts.keySet());
    List<String> values = keys.stream().map(texts::get).toList();
    String[] arr = values.toArray(String[]::new);

    Translation[] enTranslations = translateToEn(arr).getTranslations();
    Translation[] ruTranslations = translateToRu(arr).getTranslations();
    Translation[] zhTranslations = translateToZh(arr).getTranslations();
    Translation[] hiTranslations = translateToHi(arr).getTranslations();

    Map<String, Map<String, String>> result = new HashMap<>();
    for (int i = 0; i < keys.size(); i++) {
      Map<String, String> translation = new HashMap<>();
      translation.put("en", enTranslations[i].getText());
      translation.put("ru", ruTranslations[i].getText());
      translation.put("zh", zhTranslations[i].getText());
      translation.put("hi", hiTranslations[i].getText());
      result.put(keys.get(i), translation);
    }
    return result;
  }

  // ── core translation logic ──────────────────────────────────────────────────

  private TranslationResponse translateInternal(String targetLanguage, String sourceLanguage,
                                                String... texts) {
    if (texts == null || texts.length == 0) {
      return new YandexTranslationResponse(new YandexTranslation[] {});
    }

    String prompt = buildPrompt(targetLanguage, sourceLanguage, texts);
    log.debug("DeepSeek prompt: {}", prompt);

    String raw = deepSeekService.generateResponse(prompt);
    log.debug("DeepSeek raw response: {}", raw);

    YandexTranslation[] translations = parseTranslations(raw, texts.length);
    return new YandexTranslationResponse(translations);
  }

  /**
   * Ask DeepSeek to return a plain JSON array of translated strings — one element per input text.
   * Using JSON makes parsing deterministic and avoids numbered-list ambiguity.
   */
  private String buildPrompt(String targetLanguage, String sourceLanguage, String[] texts) {
    String sourcePart = StringUtils.isNotBlank(sourceLanguage)
        ? String.format("Source language: %s. ", sourceLanguage)
        : "";

    String textsJson;
    try {
      textsJson = objectMapper.writeValueAsString(texts);
    } catch (JsonProcessingException e) {
      // Fallback: manual join (should never happen for plain strings)
      textsJson = Arrays.toString(texts);
    }

    return String.format(
        """
        %sTranslate each element of the following JSON array to language code "%s".
        Return ONLY a valid JSON array of translated strings in the same order, no extra text.
        Input: %s
        """,
        sourcePart, targetLanguage, textsJson
    );
  }

  /**
   * Parse JSON array returned by the model.
   * Falls back gracefully: if only one text was requested and parsing fails,
   * the raw response itself is used as the translation.
   */
  private YandexTranslation[] parseTranslations(String raw, int expectedCount) {
    String cleaned = raw == null ? "" : raw.strip();

    // Strip accidental markdown code fences
    if (cleaned.startsWith("```")) {
      cleaned = cleaned.replaceAll("(?s)^```[a-z]*\\n?", "").replaceAll("```$", "").strip();
    }

    try {
      List<String> list = objectMapper.readValue(cleaned, new TypeReference<>() {
      });
      return list.stream()
          .map(t -> new YandexTranslation(t, null))
          .toArray(YandexTranslation[]::new);
    } catch (Exception e) {
      log.warn("Failed to parse DeepSeek JSON response, falling back. Response: {}", cleaned, e);

      // Single-text fallback: use the raw response as-is
      if (expectedCount == 1) {
        return new YandexTranslation[] {new YandexTranslation(cleaned, null)};
      }

      // Multi-text fallback: fill all slots with empty string to avoid NPE upstream
      YandexTranslation[] fallback = new YandexTranslation[expectedCount];
      for (int i = 0; i < expectedCount; i++) {
        fallback[i] = new YandexTranslation("", null);
      }
      return fallback;
    }
  }

  // ── internal helpers ────────────────────────────────────────────────────────

  private String getPrimaryLocaleText(Map<String, String> translations) {
    // Mirror Yandex priority: ru → en → zh → hi
    for (String lang : List.of("ru", "en", "zh", "hi")) {
      String val = StringUtils.trimToNull(translations.get(lang));
      if (val != null) {
        return val;
      }
    }
    throw new com.surofu.exporteru.application.exception.EmptyTranslationException();
  }
}