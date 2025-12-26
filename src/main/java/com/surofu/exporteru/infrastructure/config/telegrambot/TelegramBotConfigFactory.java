package com.surofu.exporteru.infrastructure.config.telegrambot;

import com.surofu.exporteru.application.components.telegrambot.KeyboardBuilder;
import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLinkAccountHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLoginHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotRegisterHandler;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
public class TelegramBotConfigFactory {
  private final UserRepository userRepository;
  private final KeyboardBuilder keyboardBuilder;
  private final LocalizationManager localizationManager;
  private final TelegramBotRegisterHandler telegramBotRegisterHandler;
  private final TelegramBotLoginHandler telegramBotLoginHandler;
  private final TelegramBotLinkAccountHandler telegramBotLinkAccountHandler;

  public TelegramBotConfigFactory(
      UserRepository userRepository,
      KeyboardBuilder keyboardBuilder,
      LocalizationManager localizationManager,
      @Lazy
      TelegramBotRegisterHandler telegramBotRegisterHandler,
      @Lazy
      TelegramBotLoginHandler telegramBotLoginHandler,
      @Lazy
      TelegramBotLinkAccountHandler telegramBotLinkAccountHandler
  ) {
    this.userRepository = userRepository;
    this.keyboardBuilder = keyboardBuilder;
    this.localizationManager = localizationManager;
    this.telegramBotRegisterHandler = telegramBotRegisterHandler;
    this.telegramBotLoginHandler = telegramBotLoginHandler;
    this.telegramBotLinkAccountHandler = telegramBotLinkAccountHandler;
  }

  @Bean
  public TelegramLongPollingBot russianTelegramLongPollingBot(
      @Qualifier("russianTelegramBotConfig") RussianTelegramBotConfig config) {
    return new TelegramBot(config, userRepository, keyboardBuilder, localizationManager,
        telegramBotRegisterHandler, telegramBotLoginHandler, telegramBotLinkAccountHandler);
  }

  @Bean
  public TelegramLongPollingBot englishTelegramLongPollingBot(
      @Qualifier("englishTelegramBotConfig") EnglishTelegramBotConfig config) {
    return new TelegramBot(config, userRepository, keyboardBuilder, localizationManager,
        telegramBotRegisterHandler, telegramBotLoginHandler, telegramBotLinkAccountHandler);
  }

  @Bean
  public TelegramLongPollingBot chinaTelegramLongPollingBot(
      @Qualifier("chinaTelegramBotConfig") ChinaTelegramBotConfig config) {
    return new TelegramBot(config, userRepository, keyboardBuilder, localizationManager,
        telegramBotRegisterHandler, telegramBotLoginHandler, telegramBotLinkAccountHandler);
  }

  @Primary
  @Bean(name = "russianTelegramBot")
  public TelegramBot russianTelegramBot(
      @Qualifier("russianTelegramLongPollingBot") TelegramLongPollingBot bot) {
    return (TelegramBot) bot;
  }

  @Bean(name = "englishTelegramBot")
  public TelegramBot englishTelegramBot(
      @Qualifier("englishTelegramLongPollingBot") TelegramLongPollingBot bot) {
    return (TelegramBot) bot;
  }

  @Bean(name = "chinaTelegramBot")
  public TelegramBot chinaTelegramBot(
      @Qualifier("chinaTelegramLongPollingBot") TelegramLongPollingBot bot) {
    return (TelegramBot) bot;
  }
}
