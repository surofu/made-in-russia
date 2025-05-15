package com.surofu.madeinrussia.application.command.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record LoginWithEmailCommand(
        @Validated
        @NotNull(message = "Неверная почта или пароль")
        @NotBlank(message = "Неверная почта или пароль")
        String email,

        @Validated
        @NotNull(message = "Неверная почта или пароль")
        @NotBlank(message = "Неверная почта или пароль")
        @Length(min = 4, message = "Неверная почта или пароль")
        String password) {
}
