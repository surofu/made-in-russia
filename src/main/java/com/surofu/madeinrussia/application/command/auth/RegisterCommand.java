package com.surofu.madeinrussia.application.command.auth;

import jakarta.annotation.Nullable;

public record RegisterCommand(
        String email,
        String login,
        String password,
        String region,
        @Nullable
        String phoneNumber,
        String avatarUrl
) {
}
