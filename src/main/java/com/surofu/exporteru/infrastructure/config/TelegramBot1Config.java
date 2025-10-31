package com.surofu.exporteru.infrastructure.config;

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
public class TelegramBot1Config implements TelegramBotConfig {
    @Value("${telegram.bot.username}")
    String username;
    @Value("${telegram.bot.token}")
    String token;
}
