package com.surofu.madeinrussia.core.model.telegram;

import java.time.LocalDate;

public record TelegramUser(
        Long id,
        String firstName,
        String lastName,
        String photoUrl,
        LocalDate authDate,
        String hash
) {
}
