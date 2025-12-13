package com.surofu.exporteru.application.command.auth;

public record VerifyRecoverPasswordCommand(String email, String recoverCode) {
}
