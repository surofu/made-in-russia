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

  private static final Map<String, String> LANGUAGE_NAMES = Map.of(
      "en", "English",
      "ru", "Russian",
      "zh", "Chinese (Simplified, using Han characters)",
      "hi", "Hindi"
  );

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

  private TranslationResponse translateInternal(String targetLanguage, String sourceLanguage,
                                                String... texts) {
    if (texts == null || texts.length == 0) {
      return new YandexTranslationResponse(new YandexTranslation[] {});
    }

    String prompt = buildPrompt(targetLanguage, sourceLanguage, texts);
    log.debug("DeepSeek prompt: {}", prompt);

    String raw = deepSeekService.generateResponse(prompt);
    log.info("DeepSeek raw response for target={}: {}", targetLanguage, raw);

    YandexTranslation[] translations = parseTranslations(raw, texts.length, targetLanguage);
    return new YandexTranslationResponse(translations);
  }

  private String buildPrompt(String targetLanguage, String sourceLanguage, String[] texts) {
    String targetName = LANGUAGE_NAMES.getOrDefault(targetLanguage, targetLanguage);

    String sourcePart = StringUtils.isNotBlank(sourceLanguage)
        ? String.format("Source language: %s. ",
        LANGUAGE_NAMES.getOrDefault(sourceLanguage, sourceLanguage))
        : "";

    String textsJson;
    try {
      textsJson = objectMapper.writeValueAsString(texts);
    } catch (JsonProcessingException e) {
      textsJson = Arrays.toString(texts);
    }

    return String.format(
        """
        %sTranslate each element of the following JSON array into %s (language code "%s").
        You MUST translate every element, even if it looks similar to the target script.
        If the target language is Chinese, the output MUST contain Chinese (Han) characters \
        and MUST NOT contain Cyrillic or Latin text copied from the source.
        Return ONLY a valid JSON array of translated strings in the same order, no extra text.
        Input: %s
        """,
        sourcePart, targetName, targetLanguage, textsJson
    );
  }

  private YandexTranslation[] parseTranslations(String raw, int expectedCount,
                                                String targetLanguage) {
    String cleaned = raw == null ? "" : raw.strip();

    if (cleaned.startsWith("```")) {
      cleaned = cleaned.replaceAll("(?s)^```[a-z]*\\n?", "").replaceAll("```$", "").strip();
    }

    try {
      List<String> list = objectMapper.readValue(cleaned, new TypeReference<>() {
      });

      if ("zh".equals(targetLanguage)) {
        for (int i = 0; i < list.size(); i++) {
          String t = list.get(i);
          if (t != null && !t.isBlank() && !containsHanCharacters(t)) {
            log.warn("DeepSeek returned non-Chinese text for target=zh at index {}: {}", i, t);
          }
        }
      }

      return list.stream()
          .map(t -> new YandexTranslation(t, null))
          .toArray(YandexTranslation[]::new);
    } catch (Exception e) {
      log.error("Failed to parse DeepSeek JSON response for target={}, falling back. Response: {}",
          targetLanguage, cleaned, e);

      if (expectedCount == 1) {
        return new YandexTranslation[] {new YandexTranslation(cleaned, null)};
      }

      YandexTranslation[] fallback = new YandexTranslation[expectedCount];
      for (int i = 0; i < expectedCount; i++) {
        fallback[i] = new YandexTranslation("", null);
      }
      return fallback;
    }
  }

  private boolean containsHanCharacters(String text) {
    return text.codePoints().anyMatch(
        cp -> Character.UnicodeScript.of(cp) == Character.UnicodeScript.HAN);
  }

  private String getPrimaryLocaleText(Map<String, String> translations) {
    for (String lang : List.of("ru", "en", "zh", "hi")) {
      String val = StringUtils.trimToNull(translations.get(lang));
      if (val != null) {
        return val;
      }
    }
    throw new com.surofu.exporteru.application.exception.EmptyTranslationException();
  }
}