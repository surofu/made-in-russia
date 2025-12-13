package com.surofu.exporteru.infrastructure.config.telegrambot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChinaTelegramBotConfig implements TelegramBotConfig {
    @Value("${telegram.bot.china.enable}")
    Boolean enable;
    @Value("${telegram.bot.china.username}")
    String username;
    @Value("${telegram.bot.china.token}")
    String token;
}
