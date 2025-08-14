package com.surofu.madeinrussia.infrastructure.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotConfig {
    @Value("${telegram.bot.username}")
    String botUsername;
    @Value("${telegram.bot.token}")
    String botToken;
}
