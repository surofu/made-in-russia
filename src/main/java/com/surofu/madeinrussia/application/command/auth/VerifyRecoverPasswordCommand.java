package com.surofu.madeinrussia.application.command.auth;

public record VerifyRecoverPasswordCommand(
        String email,
        String recoverCode
) {
}
