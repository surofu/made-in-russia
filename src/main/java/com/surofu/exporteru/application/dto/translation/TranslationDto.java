package com.surofu.exporteru.application.dto.translation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

// TODO: Remove TranslationDto -> Migrate to Jsonb
@Schema(description = "Contains multilingual translations for a text field")
public record TranslationDto(
        @Schema(
                description = "English translation",
                example = "Summer Sale"
        )
        String en,

        @Schema(
                description = "Russian translation",
                example = "Летняя распродажа"
        )
        String ru,

        @Schema(
                description = "Chinese translation",
                example = "夏季大促销"
        )
        String zh
) implements Serializable {
    public static TranslationDto of(HstoreTranslationDto dto) {
        if (dto == null) {
            return null;
        }

        return new TranslationDto(
                dto.textEn(),
                dto.textRu(),
                dto.textZh()
        );
    }
}