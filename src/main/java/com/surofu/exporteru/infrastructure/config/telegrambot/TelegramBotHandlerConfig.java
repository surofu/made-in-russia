package com.surofu.exporteru.infrastructure.config.telegrambot;

import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLinkAccountHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLoginHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotRegisterHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class TelegramBotHandlerConfig {

  @Bean
  @DependsOn({"russianTelegramBot", "englishTelegramBot", "chinaTelegramBot"})
  public ApplicationRunner initializeTelegramBots(
      @Qualifier("russianTelegramBot") TelegramBot russianBot,
      @Qualifier("englishTelegramBot") TelegramBot englishBot,
      @Qualifier("chinaTelegramBot") TelegramBot chinaBot,
      TelegramBotRegisterHandler registerHandler,
      TelegramBotLoginHandler loginHandler,
      TelegramBotLinkAccountHandler linkAccountHandler) {

    return args -> {
      russianBot.setHandlers(registerHandler, loginHandler, linkAccountHandler);
      englishBot.setHandlers(registerHandler, loginHandler, linkAccountHandler);
      chinaBot.setHandlers(registerHandler, loginHandler, linkAccountHandler);
    };
  }
}
