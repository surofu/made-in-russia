package com.surofu.exporteru.infrastructure.config.telegrambot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RussianTelegramBotConfig implements TelegramBotConfig {
    @Value("${telegram.bot.russian.enable}")
    Boolean enable;
    @Value("${telegram.bot.russian.username}")
    String username;
    @Value("${telegram.bot.russian.token}")
    String token;
}
