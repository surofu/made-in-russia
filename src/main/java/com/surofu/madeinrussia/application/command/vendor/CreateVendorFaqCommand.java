package com.surofu.madeinrussia.application.command.vendor;

public record CreateVendorFaqCommand(
        String question,
        String answer
) {
}
