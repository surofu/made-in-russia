package com.surofu.exporteru.application.command.faq;

import com.surofu.exporteru.application.dto.translation.TranslationDto;

public record CreateFaqCommand(
        String question,
        TranslationDto questionTranslations,
        String answer,
        TranslationDto answerTranslations
) {
}
