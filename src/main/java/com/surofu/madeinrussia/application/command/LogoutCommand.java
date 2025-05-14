package com.surofu.madeinrussia.application.command;

public record LogoutCommand(
        String userAgent,
        String ipAddress
) {
}
