package com.surofu.exporteru.application.command.auth;

public record RecoverPasswordCommand(String email, String newPassword) {
}
