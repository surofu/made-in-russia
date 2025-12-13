package com.surofu.exporteru.application.command.vendor;

public record CreateVendorFaqCommand(
        String question,
        String answer
) {
}
