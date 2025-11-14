package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

// TODO: Remove Hstore -> Migrate to Jsonb
public class HstoreParser {
  private static final Pattern HSTORE_PATTERN = Pattern.compile(
      "\"((?:[^\\\\\"]|\\\\.)*+)\"\\s*=>\\s*\"((?:[^\\\\\"]|\\\\.)*+)\""
  );
  private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "ru", "zh");

  public static @NotNull String toString(@Nullable HstoreTranslationDto translation) {
    String emptyHstore = "\"en\"=>\"\",\"ru\"=>\"\",\"zh\"=>\"\"";

    if (translation == null) {
      return emptyHstore;
    }

    boolean hasAnyContent =
        (translation.textEn() != null && !translation.textEn().trim().isEmpty()) ||
            (translation.textRu() != null && !translation.textRu().trim().isEmpty()) ||
            (translation.textZh() != null && !translation.textZh().trim().isEmpty());

    if (!hasAnyContent) {
      return emptyHstore;
    }

    List<String> pairs = new ArrayList<>();

    if (translation.textEn() != null && !translation.textEn().trim().isEmpty()) {
      String escapedEn = escapeHstoreValue(translation.textEn());
      pairs.add(String.format("\"en\"=>\"%s\"", escapedEn));
    }

    if (translation.textRu() != null && !translation.textRu().trim().isEmpty()) {
      String escapedRu = escapeHstoreValue(translation.textRu());
      pairs.add(String.format("\"ru\"=>\"%s\"", escapedRu));
    }

    if (translation.textZh() != null && !translation.textZh().trim().isEmpty()) {
      String escapedZh = escapeHstoreValue(translation.textZh());
      pairs.add(String.format("\"zh\"=>\"%s\"", escapedZh));
    }

    return pairs.isEmpty() ? emptyHstore : String.join(", ", pairs);
  }

  public static @NotNull HstoreTranslationDto fromString(@Nullable String str) {
    if (str == null || str.trim().isEmpty()) {
      return HstoreTranslationDto.empty();
    }

    Map<String, String> hstoreMap = parseHstore(str);

    String en = unescapeHstoreValue(hstoreMap.getOrDefault("en", ""));
    String ru = unescapeHstoreValue(hstoreMap.getOrDefault("ru", ""));
    String zh = unescapeHstoreValue(hstoreMap.getOrDefault("zh", ""));

    return new HstoreTranslationDto(en, ru, zh);
  }

  /**
   * Экранирование специальных символов для hstore значения
   * Правила экранирования для PostgreSQL hstore:
   * - " экранируется как \"
   * - \ экранируется как \\
   * - управляющие символы остаются как есть (PostgreSQL hstore их нормально обрабатывает)
   */
  private static String escapeHstoreValue(@Nullable String value) {
    if (value == null) {
      return "";
    }

    // Сначала экранируем обратные слеши, потом кавычки
    return value.replace("\\", "\\\\")
        .replace("\"", "\\\"");
  }

  /**
   * Убираем экранирование из hstore значения
   */
  private static String unescapeHstoreValue(@Nullable String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    // Используем простой посимвольный разбор вместо replace
    StringBuilder result = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c == '\\' && i + 1 < value.length()) {
        char next = value.charAt(i + 1);
        if (next == '\\' || next == '"') {
          result.append(next);
          i++; // Пропускаем следующий символ
          continue;
        }
      }
      result.append(c);
    }
    return result.toString();
  }

  /**
   * Парсинг hstore строки в Map с улучшенной обработкой
   */
  private static Map<String, String> parseHstore(@Nullable String hstore) {
    Map<String, String> result = new HashMap<>();

    if (hstore == null || hstore.trim().isEmpty()) {
      return result;
    }

    Matcher matcher = HSTORE_PATTERN.matcher(hstore);

    while (matcher.find()) {
      String key = unescapeHstoreValue(matcher.group(1));
      String value = unescapeHstoreValue(matcher.group(2));

      if (SUPPORTED_LANGUAGES.contains(key)) {
        result.put(key, value);
      }
    }

    return result;
  }
}