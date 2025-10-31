package com.surofu.exporteru.infrastructure.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBot2Config implements TelegramBotConfig {
    @Value("${telegram.bot2.username}")
    String username;
    @Value("${telegram.bot2.token}")
    String token;
}
