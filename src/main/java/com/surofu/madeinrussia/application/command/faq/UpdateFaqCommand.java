package com.surofu.madeinrussia.application.command.faq;

public record UpdateFaqCommand(
        String question,
        String answer
) {
}
