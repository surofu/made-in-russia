package com.surofu.madeinrussia.application.command;

public record SaveOrUpdateSessionCommand(
        String userAgent,
        String ipAddress
) {
}
