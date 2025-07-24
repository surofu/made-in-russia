package com.surofu.madeinrussia.application.command.advertisement;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;

import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command for saving an advertisement")
public record SaveAdvertisementCommand(
        @Schema(
                description = "Main title of the advertisement",
                example = "Summer Sale",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(
                description = "Title translations in multiple languages",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TranslationDto titleTranslations,

        @Schema(
                description = "Subtitle of the advertisement",
                example = "Up to 50% off",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String subtitle,

        @Schema(
                description = "Subtitle translations in multiple languages",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TranslationDto subtitleTranslations,

        @Schema(
                description = "Additional descriptive text",
                example = "Limited time offer",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String thirdText,

        @Schema(
                description = "Translations for the third text",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        TranslationDto thirdTextTranslations,

        @Schema(
                description = "Whether the ad is displayed in a large format",
                example = "true",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Boolean isBig,

        @Schema(
                description = "Expiration date/time of the ad (ISO-8601 format)",
                example = "2025-12-31T23:59:59Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        ZonedDateTime expirationDate
) {}
