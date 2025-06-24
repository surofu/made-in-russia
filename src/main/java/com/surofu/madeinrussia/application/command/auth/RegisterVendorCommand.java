package com.surofu.madeinrussia.application.command.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public record RegisterVendorCommand(
        @Validated
        @NotNull(message = "Почта не может быть пустой")
        @NotBlank(message = "Почта не может быть пустой")
        @Email(message = "Неверный формат почты")
        @Length(max = 255, message = "Длинна почты не должна превышать 255 символа")
        String email,

        @Validated
        @NotNull(message = "Логин не может быть пустым")
        @NotBlank(message = "Логин не может быть пустым")
        @Length(max = 255, message = "Длинна логина не должна превышать 255 символа")
        String login,

        @Validated
        @NotNull(message = "Пароль не может быть пустым")
        @NotBlank(message = "Пароль не может быть пустым")
        @Length(min = 4, max = 255, message = "Длинна пароля должна быть от 4 до 255 символов")
        String password,

        @Validated
        @NotNull(message = "Номер телефона не может быть пустым")
        @NotBlank(message = "Номер телефона не может быть пустым")
        @Length(max = 255, message = "Длинна номера телефона не должна превышать 255 символа")
        String phoneNumber,

        @Validated
        @NotNull(message = "ИНН не может быть пустым")
        @NotBlank(message = "ИНН не может быть пустым")
        @Length(min = 4, max = 255, message = "Длинна ИНН должна быть от 7 до 255 символов")
        String inn,

//        @Validated
//        @Length(max = 255, message = "Длинна реквизитов не должны превышать 255 символов")
//        String paymentDetails,

        @Validated
        @NotNull(message = "Страны продавца не могут быть пустыми")
        List<String> countries,

        @Validated
        @NotNull(message = "Категории товаров продавца не могут быть пустыми")
        List<String> productCategories
) {
}
