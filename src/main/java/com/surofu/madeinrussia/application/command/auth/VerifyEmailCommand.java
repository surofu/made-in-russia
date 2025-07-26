package com.surofu.madeinrussia.application.command.auth;

public record VerifyEmailCommand(String email, String code) {
}
