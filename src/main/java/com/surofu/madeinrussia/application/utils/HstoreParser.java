package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.application.dto.HstoreTranslationDto;

public class HstoreParser {

    public static String toString(HstoreTranslationDto translation) {
        return String.format("\"en\"=>\"%s\", \"ru\"=>\"%s\", \"zh\"=>\"%s\"",
                translation.textEn(), translation.textRu(), translation.textZh());
    }

    public static HstoreTranslationDto fromString(String str) {
        String en = str.split("en\"=>\"")[1].split("\"")[0];
        String ru = str.split("ru\"=>\"")[1].split("\"")[0];
        String zh = str.split("zh\"=>\"")[1].split("\"")[0];
        return new HstoreTranslationDto(en, ru, zh);
    }
}
