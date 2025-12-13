package com.surofu.exporteru.infrastructure.config.telegrambot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnglishTelegramBotConfig implements TelegramBotConfig {
    @Value("${telegram.bot.english.enable}")
    Boolean enable;
    @Value("${telegram.bot.english.username}")
    String username;
    @Value("${telegram.bot.english.token}")
    String token;
}
