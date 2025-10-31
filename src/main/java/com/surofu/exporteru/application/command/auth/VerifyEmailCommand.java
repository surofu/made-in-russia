package com.surofu.exporteru.application.command.auth;

public record VerifyEmailCommand(String email, String code) {
}
