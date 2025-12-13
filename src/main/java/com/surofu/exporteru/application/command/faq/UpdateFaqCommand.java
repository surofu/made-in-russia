package com.surofu.exporteru.application.command.faq;

import java.util.Map;

public record UpdateFaqCommand(
        String question,
        Map<String, String> questionTranslations,
        String answer,
        Map<String, String> answerTranslations
) {
}
