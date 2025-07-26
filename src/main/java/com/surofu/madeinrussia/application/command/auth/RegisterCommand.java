package com.surofu.madeinrussia.application.command.auth;

public record RegisterCommand(
        String email,
        String login,
        String password,
        String region,
        String phoneNumber
) {
}
