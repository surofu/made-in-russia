package com.surofu.exporteru.application.command.advertisement;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.Map;

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
    Map<String, String> titleTranslations,

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
    Map<String, String> subtitleTranslations,

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
    Map<String, String> thirdTextTranslations,

    @Schema(
        description = "Link to the resource",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String link,

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
) {
}
