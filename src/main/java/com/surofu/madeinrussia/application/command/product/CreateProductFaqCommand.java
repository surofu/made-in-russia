package com.surofu.madeinrussia.application.command.product;

public record CreateProductFaqCommand(
        String question,
        String answer
) {
}
