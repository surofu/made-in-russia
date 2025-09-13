package com.surofu.madeinrussia.application.components.telegrambot;

import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.model.telegram.TelegramUser;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.infrastructure.config.TelegramBotConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot implements MessageSender {
    private final TelegramBotConfig config;
//    private final LocalizationManager localizationManager;
    private final TelegramBotRegisterHandler telegramBotRegisterHandler;
    private final TelegramBotLoginHandler telegramBotLoginHandler;
    private final TelegramBotLinkAccountHandler telegramBotLinkAccountHandler;
    private final Map<String, Function<Update, Void>> comandMap;
    private final Map<String, Function<Update, Void>> registerCallbackMap;
    private final Map<String, Function<Update, Void>> linkAccountCallbackMap;
    private final UserRepository userRepository;
    private final Map<Long, TempUserObject> userObjectMap = new ConcurrentHashMap<>();
    private final Map<Long, String> userHandlerMap = new ConcurrentHashMap<>();

    public TelegramBot(
            TelegramBotConfig config,
            LocalizationManager localizationManager,
            @Lazy TelegramBotRegisterHandler telegramBotRegisterHandler,
            @Lazy TelegramBotLoginHandler telegramBotLoginHandler,
            @Lazy TelegramBotLinkAccountHandler telegramBotLinkAccountHandler,
            UserRepository userRepository
    ) {
        super(config.getBotToken());
        this.config = config;
//        this.localizationManager = localizationManager;
        this.telegramBotRegisterHandler = telegramBotRegisterHandler;
        this.telegramBotLoginHandler = telegramBotLoginHandler;
        this.telegramBotLinkAccountHandler = telegramBotLinkAccountHandler;
        this.comandMap = new ConcurrentHashMap<>();
        this.registerCallbackMap = new ConcurrentHashMap<>();
        this.linkAccountCallbackMap = new ConcurrentHashMap<>();
        this.userRepository = userRepository;

        comandMap.put("Отмена ❌", telegramBotRegisterHandler::cancel);
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
        try {
            System.out.println("me id: " + this.getMe().getId());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
            return;
        }

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                long chatId = update.getMessage().getChatId();
                log.info("User id: {}", update.getMessage().getFrom().getId());
                log.info("Message: {}", update.getMessage().getText());

                String text = update.getMessage().getText();

                if (comandMap.containsKey(text)) {
                    comandMap.get(text).apply(update);
                } else if (userHandlerMap.containsKey(chatId)) {
                    String handler = userHandlerMap.get(chatId);

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
        return config.getBotUsername();
    }

    private void handleCallbackQuery(Update update) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        String command = update.getCallbackQuery().getData();

        if ("/login".equals(command)) {
            String callbackId = TelegramBotUtils.saveGetCallbackId(update);
            answerCallback(callbackId, "", false);

            if (!userObjectMap.containsKey(chatId)) {
                telegramBotRegisterHandler.processRegister(update);
                return;
            }

            TempUserObject userObject = userObjectMap.get(chatId);
            telegramBotLoginHandler.processLogin(update, userObject.sessionInfo, userObject.locale);
        } else if (registerCallbackMap.containsKey(command)) {
            userHandlerMap.put(chatId, "register");
            registerCallbackMap.get(command).apply(update);
        } else if (linkAccountCallbackMap.containsKey(command)) {
            if (!telegramBotLinkAccountHandler.hasUser(update)) {
                help(update);
                return;
            }

            userHandlerMap.put(chatId, "link-account");
            linkAccountCallbackMap.get(command).apply(update);
        } else {
            userHandlerMap.put(chatId, "register");
            telegramBotRegisterHandler.processRegister(update);
        }
    }

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
        sendMessage(chatId, """
                Пожалуйста, перейдите на сайт и выберете вход через телеграм там
                """, InlineKeyboardBuilder.create()
                .row()
                .urlButton("Exporteru.com", "https://exporteru.com/login")
                .build());
    }

    private void handleLogin(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
        String greeting = """
                С возвращением, %s!
                Нажмите на кнопку "Войти", чтобы выполнить вход в учетную запись Exporteru.com
                """.formatted(telegramUser.firstName());

        InlineKeyboardMarkup markup = InlineKeyboardBuilder.create()
                .row()
                .urlButton("Это был не я", "https://exporteru.com/support")
                .button("Войти", "/login")
                .build();

        userObjectMap.put(telegramUser.id(), new TempUserObject(sessionInfo, locale));

        sendMessage(telegramUser.id(), greeting, markup);
    }

    private void handleRegister(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
        String greeting = """
                Привет, %s! Добро пожаловать на Exporteru.com!
                Нажмите на кнопку "Зарегистрироваться", чтобы начать регистрацию
                """.formatted(telegramUser.firstName());

        InlineKeyboardMarkup markup = InlineKeyboardBuilder.create()
                .row()
                .button("Зарегистрироваться", "/register")
                .row()
                .button("Привязать существующий аккаунт", "/link-account")
                .build();

        telegramBotRegisterHandler.setup(telegramUser, sessionInfo, locale);
        telegramBotLinkAccountHandler.setup(telegramUser, sessionInfo, locale);

        sendMessage(telegramUser.id(), greeting, markup);
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
            Message returnedMessage = execute(message);
            log.info("Sent message to chat {}: {}", chatId, text);
            return returnedMessage;
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
    public void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) throws TelegramApiException {
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
