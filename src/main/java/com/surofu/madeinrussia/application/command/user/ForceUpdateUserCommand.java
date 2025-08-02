package com.surofu.madeinrussia.application.command.user;

public record ForceUpdateUserCommand(
        String email,
        String login,
        String phoneNumber,
        String region
) {
}
