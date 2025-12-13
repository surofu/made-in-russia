package com.surofu.exporteru.application.command.vendor;

public record UpdateVendorFaqCommand(
        String question,
        String answer
) {
}
