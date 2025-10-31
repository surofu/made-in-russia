package com.surofu.exporteru.application.command.category;

import com.surofu.exporteru.application.dto.translation.TranslationDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "SaveCategoryCommand",
        description = "DTO for creating or updating a category",
        requiredProperties = {"name", "nameTranslations", "slug"}
)
public record SaveCategoryCommand(
        @Schema(
                description = "Name of the category in the default language",
                example = "Electronics",
                minLength = 2,
                maxLength = 255
        )
        String name,

        @Schema(
                description = "Translations of the category name in different languages",
                implementation = TranslationDto.class,
                example = """
            {
                "en": "Electronics",
                "ru": "Электроника",
                "zh": "电子产品"
            }
            """
        )
        TranslationDto nameTranslations,

        @Schema(
                description = "URL-friendly identifier for the category",
                example = "electronics",
                pattern = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                minLength = 2,
                maxLength = 255
        )
        String slug,

        @Schema(
                description = "ID of the parent category (null for root categories)",
                example = "1",
                nullable = true
        )
        Long parentId,

        @Schema(
                description = "List of OKVED codes associated with the category",
                example = "[\"26.11\", \"26.12\"]",
                nullable = true
        )
        List<String> okvedCategories
) {}
