package com.surofu.exporteru.core.model.telegram;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDate;

public record TelegramUser(
        @Validated
        @NotNull
        Long id,
        @Validated
        @NotBlank
        String firstName,
        String lastName,
        String photoUrl,
        LocalDate authDate,
        String hash
) implements Serializable {
}
