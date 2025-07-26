package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import jakarta.annotation.Nullable;

import java.util.Objects;

public class HstoreParser {

    @Nullable
    public static String toString(@Nullable HstoreTranslationDto translation) {
        if (translation == null) {
            return null;
        }

        String en = Objects.requireNonNullElse(translation.textEn(), "").replace("\"", "\\\"");
        String ru = Objects.requireNonNullElse(translation.textRu(), "").replace("\"", "\\\"");
        String zh = Objects.requireNonNullElse(translation.textZh(), "").replace("\"", "\\\"");
        return String.format("\"en\"=>\"%s\", \"ru\"=>\"%s\", \"zh\"=>\"%s\"", en, ru, zh);
    }

    @Nullable
    public static HstoreTranslationDto fromString(@Nullable String str) {
        if (str == null) {
            return null;
        }

        String[] splitEn1 = str.split("en\"=>\"");
        String[] splitRu1 = str.split("ru\"=>\"");
        String[] splitZh1 = str.split("zh\"=>\"");

        String[] splitEn = splitEn1.length < 2
                ? new String[0]
                : splitEn1[1].split("\", \"ru\"");
        String[] splitRu = splitRu1.length < 2
                ? new String[0]
                : splitRu1[1].split("\", \"zh\"");
        String[] splitZh = splitZh1.length < 2
                ? new String[0]
                : splitZh1[1].split("\"$");

        String en = splitEn.length > 0 ? splitEn[0] : "";
        String ru = splitRu.length > 0 ? splitRu[0] : "";
        String zh = splitZh.length > 0 ? splitZh[0] : "";
        return new HstoreTranslationDto(en, ru, zh);
    }
}
