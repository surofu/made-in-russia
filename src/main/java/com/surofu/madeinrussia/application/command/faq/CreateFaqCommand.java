package com.surofu.madeinrussia.application.command.faq;

public record CreateFaqCommand(
        String question,
        String answer
) {
}
