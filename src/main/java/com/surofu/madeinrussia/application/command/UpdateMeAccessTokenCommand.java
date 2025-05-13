package com.surofu.madeinrussia.application.command;

import org.springframework.security.core.userdetails.UserDetails;

public record UpdateMeAccessTokenCommand(
        UserDetails userDetails,
        String refreshToken
) {
}
