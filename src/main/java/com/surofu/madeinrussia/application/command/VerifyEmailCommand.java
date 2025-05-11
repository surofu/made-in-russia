package com.surofu.madeinrussia.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

public record VerifyEmailCommand(
        @Validated
        @NotNull(message = "Почта не может быть пустой")
        @NotBlank(message = "Почта не может быть пустой")
        @Email(message = "Некорректный формат почты")
        String email,

        @Validated
        @NotNull(message = "Код подтверждения не может быть пустым")
        @NotBlank(message = "Код подтверждения не может быть пустым")
        String code
) {
}
