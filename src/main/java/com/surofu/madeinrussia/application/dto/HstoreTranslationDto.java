package com.surofu.madeinrussia.application.dto;

public record HstoreTranslationDto(
        String textEn,
        String textRu,
        String textZh
) {
    public static HstoreTranslationDto of(TranslationDto dto) {
        return new HstoreTranslationDto(dto.en(), dto.ru(), dto.zh());
    }
}
