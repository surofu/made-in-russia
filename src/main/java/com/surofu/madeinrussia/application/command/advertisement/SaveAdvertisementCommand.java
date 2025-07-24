package com.surofu.madeinrussia.application.command.advertisement;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;

import java.time.ZonedDateTime;

public record SaveAdvertisementCommand(
        String title,
        TranslationDto titleTranslations,
        String subtitle,
        TranslationDto subtitleTranslations,
        String thirdText,
        TranslationDto thirdTextTranslations,
        Boolean isBig,
        ZonedDateTime expirationDate
) {
}
