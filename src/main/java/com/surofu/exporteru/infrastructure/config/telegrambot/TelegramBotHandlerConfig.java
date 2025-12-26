package com.surofu.exporteru.infrastructure.config.telegrambot;

import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLinkAccountHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLoginHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotRegisterHandler;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotHandlerConfig {

//  @Bean
//  public ApplicationRunner initializeTelegramBots(
//      TelegramBot russianTelegramBot,
//      TelegramBot englishTelegramBot,
//      TelegramBot chinaTelegramBot,
//      TelegramBotRegisterHandler registerHandler,
//      TelegramBotLoginHandler loginHandler,
//      TelegramBotLinkAccountHandler linkAccountHandler
//  ) {
//    return args -> {
//      russianTelegramBot.setHandlers(registerHandler, loginHandler, linkAccountHandler);
//      englishTelegramBot.setHandlers(registerHandler, loginHandler, linkAccountHandler);
//      chinaTelegramBot.setHandlers(registerHandler, loginHandler, linkAccountHandler);
//    };
//  }
}
