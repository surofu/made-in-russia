package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.application.dto.HstoreTranslationDto;

import java.util.Objects;

public class HstoreParser {

    public static String toString(HstoreTranslationDto translation) {
        String en = Objects.requireNonNullElse(translation.textEn(), "").replace("\"", "\\\"");
        String ru = Objects.requireNonNullElse(translation.textRu(), "").replace("\"", "\\\"");
        String zh = Objects.requireNonNullElse(translation.textZh(), "").replace("\"", "\\\"");
        return String.format("\"en\"=>\"%s\", \"ru\"=>\"%s\", \"zh\"=>\"%s\"", en, ru, zh);
    }

    public static HstoreTranslationDto fromString(String str) {
        if (str == null) {
            return new HstoreTranslationDto("", "", "");
        }

        String[] splitEn = str.split("en\"=>\"")[1].split("\", \"ru\"");
        String[] splitRu = str.split("ru\"=>\"")[1].split("\", \"zh\"");
        String[] splitZh = str.split("zh\"=>\"")[1].split("\"$");
        String en = splitEn.length > 0 ? splitEn[0] : "";
        String ru = splitRu.length > 0 ? splitRu[0] : "";
        String zh = splitZh.length > 0 ? splitZh[0] : "";
        return new HstoreTranslationDto(en, ru, zh);
    }
}
