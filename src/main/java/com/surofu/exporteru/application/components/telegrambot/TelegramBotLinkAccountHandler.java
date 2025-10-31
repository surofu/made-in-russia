package com.surofu.exporteru.application.components.telegrambot;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.telegram.TelegramUser;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.repository.SessionRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotLinkAccountHandler {
    private final MessageSender messageSender;
    private final UserRepository userRepository;
    private final LocalizationManager localizationManager;
    private final JwtUtils jwtUtils;
    private final SessionRepository sessionRepository;
    private final Map<Long, UserObject> USERS = new ConcurrentHashMap<>();
    private final List<Function<Update, Void>> STEPS = new ArrayList<>();
    private final PasswordEncoder passwordEncoder;
    @Value("${app.frontend.oauth.telegram.redirect.success}")
    private String redirectHost;

    @PostConstruct
    public void init() {
        STEPS.addAll(Arrays.asList(
                this::stepEmailRequest,
                this::stepEmailProcess,
                this::stepPasswordRequest,
                this::stepPasswordProcess
        ));
    }

    public void setup(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
        UserObject userObject = new UserObject();
        userObject.step = null;
        userObject.sessionInfo = sessionInfo;
        userObject.locale = locale;
        USERS.put(telegramUser.id(), userObject);
    }

    public void setLocale(Update update, Locale locale) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        UserObject userObject = USERS.get(chatId);
        userObject.locale = locale;
    }

    public boolean hasUser(Update update) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        return USERS.containsKey(chatId);
    }

    public Void linkAccount(Update update) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        UserObject userObject = USERS.get(chatId);

        LocaleContextHolder.setLocale(userObject.locale);

        if (update.hasCallbackQuery()) {
            String callbackId = TelegramBotUtils.saveGetCallbackId(update);
            messageSender.answerCallback(callbackId, "", false);
        }

        if (userObject.step == null) {
            userObject.step = LinkAccountStep.first();
            return nextStep(update, 0);
        }

        return nextStep(update);
    }

    // Steps

    private Void stepEmailRequest(Update update) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        String text = localizationManager.localize("telegram.bot.register.step.email.request");
        messageSender.sendMessage(chatId, text);
        return null;
    }

    private Void stepEmailProcess(Update update) {
        // Arrange
        long chatId = TelegramBotUtils.safeGetChatId(update);
        String email = TelegramBotUtils.safeGetText(update);
        UserObject userObject = USERS.get(chatId);

        // Check
        Optional<User> userOptional = userRepository.getUserByTelegramUserId(chatId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getTelegramUserId() != null) {
                String alreadyLinkedText = localizationManager.localize("telegram.bot.error.link.already-linked");
                String markupAgain = localizationManager.localize("telegram.bot.register.step.save-data.markup.again");

                messageSender.sendMessage(chatId, alreadyLinkedText, InlineKeyboardBuilder.create()
                        .row()
                        .button(markupAgain, "/link-account")
                        .build());
                return reset(update);
            }
        }

        // Act
        userObject.email = email;

        // Next step
        return nextStep(update);
    }

    private Void stepPasswordRequest(Update update) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        String text = localizationManager.localize("telegram.bot.register.step.password.request");
        messageSender.sendMessage(chatId, text);
        return null;
    }

    @Transactional
    protected Void stepPasswordProcess(Update update) {
        // Arrange
        long chatId = TelegramBotUtils.safeGetChatId(update);
        String password = TelegramBotUtils.safeGetText(update);
        UserObject userObject = USERS.get(chatId);

        // Act
        Optional<User> userOptional = userRepository.getUserByEmail(UserEmail.of(userObject.email));

        String invalidDataText = localizationManager.localize("telegram.bot.link.invalid-data");
        String markupAgain = localizationManager.localize("telegram.bot.register.step.save-data.markup.again");

        if (userOptional.isEmpty()) {
            messageSender.sendMessage(chatId, invalidDataText, InlineKeyboardBuilder.create()
                    .row()
                    .button(markupAgain, "/link-account")
                    .build());
            return reset(update);
        }

        User user = userOptional.get();

        String errorString = localizationManager.localize("telegram.bot.error.unknown");

        if (user.getPassword() == null) {
            messageSender.sendMessage(chatId, errorString, InlineKeyboardBuilder.create()
                    .row()
                    .button(markupAgain, "/link-account")
                    .build()
            );
            return reset(update);
        }

        if (!passwordEncoder.matches(password, user.getPassword().getPassword().toString())) {
            messageSender.sendMessage(chatId, invalidDataText, InlineKeyboardBuilder.create()
                    .row()
                    .button(markupAgain, "/link-account")
                    .build());
            return reset(update);
        }

        user.setTelegramUserId(chatId);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            messageSender.sendMessage(chatId, errorString, InlineKeyboardBuilder.create()
                    .row()
                    .button(markupAgain, "/link-account")
                    .build()
            );
            return reset(update);
        }



        Session session = sessionRepository.getSessionByUserIdAndDeviceId(
                user.getId(),
                userObject.sessionInfo.getDeviceId()
        ).orElse(Session.of(userObject.sessionInfo, user, new Session()));

        try {
            sessionRepository.save(session);
        } catch (Exception e) {
            messageSender.sendMessage(chatId, errorString, InlineKeyboardBuilder.create()
                    .row()
                    .button(markupAgain, "/link-account")
                    .build()
            );
            return reset(update);
        }

        SecurityUser securityUser = new SecurityUser(user, user.getPassword(), userObject.sessionInfo);

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        String rawUrl = "%s?accessToken=%s&refreshToken=%s".formatted(
                redirectHost, accessToken, refreshToken
        );
        String formatedUrl = UriComponentsBuilder.fromUriString(rawUrl)
                .build()
                .encode()
                .toUriString();

        String linkSuccess = localizationManager.localize("telegram.bot.link.success");
        String markupRedirect = localizationManager.localize("telegram.bot.login.markup.redirect");

        messageSender.sendMessage(chatId, linkSuccess, InlineKeyboardBuilder.create()
                .row()
                .urlButton(markupRedirect, formatedUrl)
                .build());

        USERS.remove(chatId);
        return null;
    }

    // Utils

    private Void nextStep(Update update) {
        return nextStep(update, 1);
    }

    private Void nextStep(Update update, int count) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        UserObject userObject = USERS.get(chatId);
        LinkAccountStep nextStep = userObject.step.next(count);
        userObject.step = nextStep;
        STEPS.get(nextStep.index).apply(update);
        return null;
    }

    private Void reset(Update update) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        UserObject userObject = USERS.get(chatId);
        userObject.step = null;
        userObject.email = null;
        return null;
    }

    private enum LinkAccountStep {
        EMAIL_REQUEST(0),
        EMAIL_PROCESS(1),
        PASSWORD_REQUEST(2),
        PASSWORD_PROCESS(3);

        private final int index;

        LinkAccountStep(int index) {
            this.index = index;
        }

        public static LinkAccountStep first() {
            for (LinkAccountStep step : LinkAccountStep.values()) {
                if (step.index == 0) {
                    return step;
                }
            }

            throw new IllegalArgumentException("First link account step not found");
        }

        public LinkAccountStep next(int count) {
            for (LinkAccountStep step : LinkAccountStep.values()) {
                if (step.index == index + count) {
                    return step;
                }
            }

            throw new IllegalArgumentException("Link next account step not found");
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserObject {
        private LinkAccountStep step;
        private String email;
        private SessionInfo sessionInfo;
        private Locale locale;
    }
}
