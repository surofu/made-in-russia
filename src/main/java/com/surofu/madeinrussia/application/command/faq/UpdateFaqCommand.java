package com.surofu.madeinrussia.application.command.faq;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;

public record UpdateFaqCommand(
        String question,
        TranslationDto questionTranslations,
        String answer,
        TranslationDto answerTranslations
) {
}
