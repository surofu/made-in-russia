package com.surofu.exporteru.application.components.telegrambot;

import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.telegram.TelegramUser;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.infrastructure.config.telegrambot.TelegramBotConfig;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot implements MessageSender {
  private final TelegramBotConfig config;
  private final LocalizationManager localizationManager;
  private final Map<String, Function<Update, Void>> comandMap = new ConcurrentHashMap<>();
  private final Map<String, Function<Update, Void>> registerCallbackMap = new ConcurrentHashMap<>();
  private final Map<String, Function<Update, Void>> linkAccountCallbackMap =
      new ConcurrentHashMap<>();
  private final UserRepository userRepository;
  private final Map<Long, TempUserObject> userObjectMap = new ConcurrentHashMap<>();
  private final Map<String, String> userHandlerMap = new ConcurrentHashMap<>();
  private final KeyboardBuilder keyboardBuilder;
  private TelegramBotRegisterHandler telegramBotRegisterHandler;
  private TelegramBotLoginHandler telegramBotLoginHandler;
  private TelegramBotLinkAccountHandler telegramBotLinkAccountHandler;

  public TelegramBot(
      TelegramBotConfig config,
      UserRepository userRepository,
      KeyboardBuilder keyboardBuilder,
      LocalizationManager localizationManager) {
    super(config.getToken());
    this.config = config;
    this.userRepository = userRepository;
    this.keyboardBuilder = keyboardBuilder;
    this.localizationManager = localizationManager;

    // Инициализируем только базовые команды, которые не зависят от handlers
    initBasicCommands();
  }

  private void initBasicCommands() {
    comandMap.putAll(Map.of(
        "English", (Update update) -> changeLanguage(Locale.forLanguageTag("en"), update),
        "Русский", (Update update) -> changeLanguage(Locale.forLanguageTag("ru"), update),
        "中文", (Update update) -> changeLanguage(Locale.forLanguageTag("zh"), update)
    ));
  }

  // Метод для установки handlers после создания бина
  public void setHandlers(
      TelegramBotRegisterHandler registerHandler,
      TelegramBotLoginHandler loginHandler,
      TelegramBotLinkAccountHandler linkAccountHandler) {

    this.telegramBotRegisterHandler = registerHandler;
    this.telegramBotLoginHandler = loginHandler;
    this.telegramBotLinkAccountHandler = linkAccountHandler;

    // Инициализируем команды, которые зависят от handlers
    initHandlerDependentCommands();
  }

  private void initHandlerDependentCommands() {
    comandMap.putAll(Map.of(
        "Отмена ❌", telegramBotRegisterHandler::cancel,
        "Cancel ❌", telegramBotRegisterHandler::cancel,
        "取消 ❌", telegramBotRegisterHandler::cancel
    ));

    registerCallbackMap.putAll(Map.of(
        "/register", telegramBotRegisterHandler::processRegister,
        "/skip", telegramBotRegisterHandler::skipStep,
        "/save-data", telegramBotRegisterHandler::processRegister,
        "/reset", telegramBotRegisterHandler::reset
    ));

    linkAccountCallbackMap.put(
        "/link-account", telegramBotLinkAccountHandler::linkAccount
    );
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasCallbackQuery()) {
      handleCallbackQuery(update);
      return;
    }

    if (update.hasMessage()) {
      if (update.getMessage().hasText()) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (comandMap.containsKey(text)) {
          comandMap.get(text).apply(update);
        } else if (userHandlerMap.containsKey(String.valueOf(chatId))) {
          String handler = userHandlerMap.get(String.valueOf(chatId));

          if ("register".equals(handler)) {
            telegramBotRegisterHandler.processRegister(update);
          } else if ("link-account".equals(handler)) {
            telegramBotLinkAccountHandler.linkAccount(update);
          }
        } else if (telegramBotRegisterHandler.containsChat(update)) {
          telegramBotRegisterHandler.processRegister(update);
        } else {
          help(update);
        }
      }
    }
  }

  @Override
  public String getBotUsername() {
    return config.getUsername();
  }

  private void handleCallbackQuery(Update update) {
    long chatId = TelegramBotUtils.safeGetChatId(update);
    String command = update.getCallbackQuery().getData();

    if ("/login".equals(command)) {
      String callbackId = TelegramBotUtils.saveGetCallbackId(update);

      if (!userObjectMap.containsKey(chatId)) {
        telegramBotRegisterHandler.processRegister(update);
        answerCallback(callbackId, "", false);
        return;
      }

      TempUserObject userObject = userObjectMap.get(chatId);
      telegramBotLoginHandler.processLogin(update, userObject.sessionInfo, userObject.locale);
      answerCallback(callbackId, "", false);
    } else if (registerCallbackMap.containsKey(command)) {
      userHandlerMap.put(String.valueOf(chatId), "register");
      registerCallbackMap.get(command).apply(update);
    } else if (linkAccountCallbackMap.containsKey(command)) {
      if (!telegramBotLinkAccountHandler.hasUser(update)) {
        help(update);
        return;
      }

      userHandlerMap.put(String.valueOf(chatId), "link-account");
      linkAccountCallbackMap.get(command).apply(update);
    } else {
      userHandlerMap.put(String.valueOf(chatId), "register");
      telegramBotRegisterHandler.processRegister(update);
    }
  }

  // Остальные методы без изменений...
  public void authorize(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
    Optional<User> user = userRepository.getUserByTelegramUserId(telegramUser.id());

    if (user.isPresent()) {
      handleLogin(telegramUser, sessionInfo, locale);
      return;
    }

    handleRegister(telegramUser, sessionInfo, locale);
  }

  private void help(Update update) {
    long chatId = TelegramBotUtils.safeGetChatId(update);
    String text = localizationManager.localize("telegram.bot.error.help");
    sendMessage(chatId, text, InlineKeyboardBuilder.create()
        .row()
        .urlButton("Exporteru.com", "https://exporteru.com/login")
        .build());
  }

  private void handleLogin(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
    LocaleContextHolder.setLocale(locale);

    String greeting =
        localizationManager.localize("telegram.bot.login.start", telegramUser.firstName());
    String markupNotMe = localizationManager.localize("telegram.bot.login.markup.not-me");
    String markupContinueLogin = localizationManager.localize("telegram.bot.login.markup.continue");

    InlineKeyboardMarkup markup = InlineKeyboardBuilder.create()
        .row()
        .urlButton(markupNotMe, "https://exporteru.com/support")
        .button(markupContinueLogin, "/login")
        .build();

    userObjectMap.put(telegramUser.id(), new TempUserObject(sessionInfo, locale));
    sendMessage(telegramUser.id(), greeting, markup);
  }

  private void handleRegister(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
    String greeting =
        localizationManager.localize("telegram.bot.register.start", telegramUser.firstName());
    String greeting2 =
        localizationManager.localize("telegram.bot.register.start-2", telegramUser.firstName());
    String markupContinue = localizationManager.localize("telegram.bot.register.markup.begin");
    String markupLinkAccount =
        localizationManager.localize("telegram.bot.register.markup.link-account");

    InlineKeyboardMarkup markup = InlineKeyboardBuilder.create()
        .row()
        .button(markupContinue, "/register")
        .row()
        .button(markupLinkAccount, "/link-account")
        .build();

    telegramBotRegisterHandler.setup(telegramUser, sessionInfo, locale);
    telegramBotLinkAccountHandler.setup(telegramUser, sessionInfo, locale);

    sendMessage(telegramUser.id(), greeting, keyboardBuilder.createMainMenu());
    sendMessage(telegramUser.id(), greeting2, markup);
  }

  private Void changeLanguage(Locale locale, Update update) {
    LocaleContextHolder.setLocale(locale);
    telegramBotRegisterHandler.setLocale(update, locale);
    telegramBotLinkAccountHandler.setLocale(update, locale);

    long chatId = TelegramBotUtils.safeGetChatId(update);
    String text = localizationManager.localize("telegram.bot.set-language");
    sendMessage(chatId, text);
    return null;
  }

  @Override
  public Message sendMessage(Long chatId, String text) {
    return sendMessage(chatId, text, null);
  }

  @Override
  public Message sendMessage(Long chatId, String text, ReplyKeyboard markup) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId.toString());
    message.setText(text);

    if (markup != null) {
      message.setReplyMarkup(markup);
    }

    try {
      return execute(message);
    } catch (TelegramApiException e) {
      log.error("Failed to send message to chat {}", chatId, e);
      return null;
    }
  }

  @Override
  public void answerCallback(String callbackQueryId, String text, boolean showAlert) {
    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
    answerCallbackQuery.setCallbackQueryId(callbackQueryId);
    answerCallbackQuery.setText(text);
    answerCallbackQuery.setShowAlert(showAlert);

    try {
      execute(answerCallbackQuery);
    } catch (TelegramApiException e) {
      log.error("Failed to send message to answer callback query {}", callbackQueryId, e);
    }
  }

  @Override
  public void editMessageText(EditMessageText editMessageText) throws TelegramApiException {
    execute(editMessageText);
  }

  @Override
  public void deleteMessage(DeleteMessage deleteMessage) throws TelegramApiException {
    execute(deleteMessage);
  }

  @Override
  public void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup)
      throws TelegramApiException {
    execute(editMessageReplyMarkup);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class TempUserObject {
    SessionInfo sessionInfo;
    Locale locale;
  }
}
