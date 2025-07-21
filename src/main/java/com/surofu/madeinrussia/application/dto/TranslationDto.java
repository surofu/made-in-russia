package com.surofu.madeinrussia.application.dto;

public record TranslationDto(
        String en,
        String ru,
        String zh
) {

    public static TranslationDto of(HstoreTranslationDto dto) {
        return new TranslationDto(dto.textEn(), dto.textRu(), dto.textZh());
    }
}
