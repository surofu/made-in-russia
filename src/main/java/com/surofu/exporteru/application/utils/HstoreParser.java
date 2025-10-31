package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import jakarta.annotation.Nullable;

import java.util.Objects;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class HstoreParser {
    private static final Pattern HSTORE_PATTERN = Pattern.compile("\"((?:\\\\\"|[^\"])+)\"\\s*=>\\s*\"((?:\\\\\"|[^\"])*)\"");
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "ru", "zh");

    @Nullable
    public static String toString(@Nullable HstoreTranslationDto translation) {
        if (translation == null) {
            return null;
        }

        // Check if all translations are null or empty
        boolean hasAnyContent = (translation.textEn() != null && !translation.textEn().trim().isEmpty()) ||
                (translation.textRu() != null && !translation.textRu().trim().isEmpty()) ||
                (translation.textZh() != null && !translation.textZh().trim().isEmpty());

        if (!hasAnyContent) {
            return null;
        }

        List<String> pairs = new ArrayList<>();

        // Добавляем только не-null и не-пустые значения
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

        return pairs.isEmpty() ? null : String.join(", ", pairs);
    }

    @Nullable
    public static HstoreTranslationDto fromString(@Nullable String str) {
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
    private static String escapeHstoreValue(String value) {
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
    private static String unescapeHstoreValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean escape = false;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (escape) {
                switch (c) {
                    case '\\' -> result.append('\\');
                    case '"' -> result.append('"');
                    // Для других escape-последовательностей оставляем как есть
                    default -> result.append('\\').append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else {
                result.append(c);
            }
        }

        // Если строка закончилась на escape-символ, добавляем его
        if (escape) {
            result.append('\\');
        }

        return result.toString();
    }

    /**
     * Парсинг hstore строки в Map с улучшенной обработкой
     */
    private static Map<String, String> parseHstore(String hstore) {
        Map<String, String> result = new HashMap<>();

        if (hstore == null || hstore.trim().isEmpty()) {
            return result;
        }

        Matcher matcher = HSTORE_PATTERN.matcher(hstore);

        while (matcher.find()) {
            String key = unescapeHstoreValue(matcher.group(1));
            String value = matcher.group(2); // value будет unescaped в unescapeHstoreValue

            if (SUPPORTED_LANGUAGES.contains(key)) {
                result.put(key, value);
            }
        }

        return result;
    }
}
