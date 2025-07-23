package com.surofu.madeinrussia.application.command.advertisement;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;

public record SaveAdvertisementCommand(
        String title,
        TranslationDto titleTranslations,
        String subtitle,
        TranslationDto subtitleTranslations
) {
}
