package com.surofu.madeinrussia.application.command.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record RegisterCommand(
        @Validated
        @NotNull(message = "Почта не может быть пустой")
        @NotBlank(message = "Почта не может быть пустой")
        @Email(message = "Неверный формат почты")
        @Length(max = 255, message = "Длина почты не должна превышать 255 символа")
        String email,

        @Validated
        @NotNull(message = "Логин не может быть пустым")
        @NotBlank(message = "Логин не может быть пустым")
        @Length(max = 255, message = "Длина логина не должна превышать 255 символа")
        String login,

        @Validated
        @NotNull(message = "Пароль не может быть пустым")
        @NotBlank(message = "Пароль не может быть пустым")
        @Length(min = 4, max = 255, message = "Длина пароля должна быть от 4 до 255 символов")
        String password,

        @Validated
        @NotNull(message = "Регион не может быть пустым")
        @NotBlank(message = "Регион не может быть пустым")
        @Length(max = 255, message = "Длина региона не должна превышать 255 символа")
        String region,

        @Validated
        @NotNull(message = "Номер телефона не может быть пустым")
        @NotBlank(message = "Номер телефона не может быть пустым")
        @Length(max = 255, message = "Длина номера телефона не должна превышать 255 символа")
        String phoneNumber
) {
}
