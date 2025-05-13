package com.surofu.madeinrussia.application.command;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

public record RefreshMeCurrentSessionCommand(
        @Validated
        @NotNull(message = "Токен обновления не может быть пустым")
        @NotEmpty(message = "Токен обновления не может быть пустым")
        String refreshToken
) {
}
