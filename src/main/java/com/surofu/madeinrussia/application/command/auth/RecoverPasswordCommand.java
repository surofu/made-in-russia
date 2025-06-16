package com.surofu.madeinrussia.application.command.auth;

public record RecoverPasswordCommand(
        String email,
        String newPassword
) {
}
