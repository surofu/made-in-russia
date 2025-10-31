package com.surofu.exporteru.application.dto.translation;

import java.io.Serializable;

public record HstoreTranslationDto(
        String textEn,
        String textRu,
        String textZh
) implements Serializable {
    public static HstoreTranslationDto empty() {
        return new HstoreTranslationDto("", "", "");
    }

    public static HstoreTranslationDto of(TranslationDto dto) {
        if (dto == null) {
            return empty();
        }

        return new HstoreTranslationDto(dto.en(), dto.ru(), dto.zh());
    }

    public static HstoreTranslationDto ofNullable(TranslationDto dto) {
        return dto != null ? of(dto) : null;
    }
}
