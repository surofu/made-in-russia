package com.surofu.madeinrussia.application.command.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record LoginWithLoginCommand(
        @Validated
        @NotNull(message = "Неверный логин или пароль")
        @NotBlank(message = "Неверный логин или пароль")
        String login,

        @Validated
        @NotNull(message = "Неверный логин или пароль")
        @NotBlank(message = "Неверный логин или пароль")
        @Length(min = 4, message = "Неверный логин или пароль")
        String password) {
}
