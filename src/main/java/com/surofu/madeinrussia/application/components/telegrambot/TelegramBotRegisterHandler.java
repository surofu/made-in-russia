package com.surofu.madeinrussia.application.components.telegrambot;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.utils.AuthUtils;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.telegram.TelegramUser;
import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.mail.MailService;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.JpaVendorCountryRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.surofu.madeinrussia.application.components.telegrambot.TelegramBotUtils.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotRegisterHandler {
    private final MessageSender messageSender;
    private final Map<Long, RegisterObject> history = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtUtils jwtUtils;
    private final Map<String, String> COUNTRIES = new ConcurrentHashMap<>();
    private final TranslationRepository translationRepository;
    private final JpaVendorCountryRepository vendorCountryRepository;
    @Value("${app.redis.verification-ttl-duration}")
    private Duration verificationTtl;
    @Value("${app.frontend.oauth.telegram.redirect.success}")
    private String redirectSuccessHost;

    private final List<Function<Update, Void>> stepFunctions = List.of(
            this::stepBegin,
            this::stepAccountTypeRequest,
            this::stepAccountTypeProcess,
            this::stepLoginRequest,
            this::stepLoginProcess,
            this::stepEmailRequest,
            this::stepEmailProcess,
            this::stepRegionRequest,
            this::stepRegionProcess,
            this::stepPhoneNumberRequest,
            this::stepPhoneNumberProcess,
            this::stepInnRequest,
            this::stepInnProcess,
            this::stepVendorCountriesRequest,
            this::stepVendorCountriesProcess,
            this::stepPasswordRequest,
            this::stepPasswordProcess,
            this::stepConfirmPasswordRequest,
            this::stepConfirmPasswordProcess,
            this::stepVerifyEmailSendMail,
            this::stepVerifyEmailRequest,
            this::stepVerifyEmailProcess,
            this::stepSaveDataRequest,
            this::stepSaveDataProcess
    );

    @PostConstruct
    public void init() {
        COUNTRIES.putAll(Map.of(
                "Russia", "Россия \uD83C\uDDF7\uD83C\uDDFA",
                "Belarus", "Беларусь \uD83C\uDDE7\uD83C\uDDFE",
                "Kazakhstan", "Казахстан \uD83C\uDDF0\uD83C\uDDFF",
                "China", "Китай \uD83C\uDDE8\uD83C\uDDF3"
        ));
    }

    public void setup(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
        history.put(telegramUser.id(), new RegisterObject(
                RegisterStep.BEGIN,
                new RegisterRequest(),
                sessionInfo,
                locale
        ));
    }

    public void error(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, """
                Не удалось обработать ваш ответ
                """, InlineKeyboardBuilder.create()
                        .row()
                        .button("Начать сначала", "/reset")
                .build());
    }

    public boolean containsChat(Update update) {
        long chatId = safeGetChatId(update);
        return history.containsKey(chatId);
    }

    public Void skipStep(Update update) {
        // Answer
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);

        // Act
        nextOrFirstStep(update, 2);
        return null;
    }

    public void clearSteps(Update update) {
        long chatId = safeGetChatId(update);

        if (history.containsKey(chatId)) {
            history.get(chatId).step = RegisterStep.BEGIN;
            history.get(chatId).request = new RegisterRequest();
            history.get(chatId).emailVerificationCode = null;
        }
    }

    public Void reset(Update update) {
        clearSteps(update);
        processRegister(update);
        return null;
    }

    public void help(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, """
                Пожалуйста, перейдите на сайт и выберете вход через телеграм там
                """, InlineKeyboardBuilder.create()
                .row()
                .urlButton("Exporteru.com", "https://exporteru.com/login")
                .build());
    }

    public Void processRegister(Update update) {
        // Answer
        try {
            String callbackId = saveGetCallbackId(update);
            messageSender.answerCallback(callbackId, "", false);
        } catch (Exception ignored) {}

        nextOrFirstStep(update);
        return null;
    }

    private Void stepBegin(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, "Начало регистрации", KeyboardBuilder.createMainMenu());

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepAccountTypeRequest(Update update) {
        long chatId = safeGetChatId(update);

        // Answer
        try {
            String callbackId = saveGetCallbackId(update);
            messageSender.answerCallback(callbackId, "", false);
        } catch (Exception ignored) {}

        messageSender.sendMessage(chatId, "Выберите тип аккаунта", InlineKeyboardBuilder.create()
                .row()
                .button("Я покупатель", "buyer")
                .button("Я продавец", "vendor")
                .build());
        return null;
    }

    private Void stepAccountTypeProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String accountType = safeGetText(update);
        RegisterObject registerObject = history.get(chatId);

        // Answer
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);

        // Process
        if ("vendor".equals(accountType)) {
            registerObject.request.userRole = UserRole.ROLE_VENDOR;
        } else {
            registerObject.request.userRole = UserRole.ROLE_USER;
        }

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepLoginRequest(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, "Пришлите логин");
        return null;
    }

    private Void stepLoginProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String login = safeGetText(update);

        // Validate
        if (StringUtils.trimToNull(login) == null) {
            messageSender.sendMessage(chatId, "Логин не может быть пустым");
            prevOrFirstStep(update);
            return null;
        }

        if (login.length() < 2 || login.length() > 255) {
            messageSender.sendMessage(chatId, "Логин должен содержать от 2 до 255 символов");
            prevOrFirstStep(update);
            return null;
        }

        // Setting
        history.get(chatId).request.login = login.trim();

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepEmailRequest(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, "Пришлите почту");
        return null;
    }

    @Transactional(readOnly = true)
    protected Void stepEmailProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String email = safeGetText(update).trim().toLowerCase();

        // Validate
        if (StringUtils.trimToNull(email) == null) {
            messageSender.sendMessage(chatId, "Почта не может быть пустой");
            prevOrFirstStep(update);
            return null;
        }

        if (email.length() < 2 || email.length() > 255) {
            messageSender.sendMessage(chatId, "Почта должна содержать от 2 до 255 символов");
            prevOrFirstStep(update);
            return null;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            messageSender.sendMessage(chatId, "Почта должна быть валидной");
            prevOrFirstStep(update);
            return null;
        }

        if (userExistsByEmail(email)) {
            messageSender.sendMessage(chatId, "Пользователь с почтой \"%s\" уже существует".formatted(email));
            prevOrFirstStep(update);
            return null;
        }

        // Setting
        history.get(chatId).request.email = email;

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepRegionRequest(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        RegisterObject registerObject = history.get(chatId);

        // Process
        if (UserRole.ROLE_VENDOR.equals(registerObject.request.userRole)) {
            nextOrFirstStep(update, 2);
            return null;
        }

        messageSender.sendMessage(chatId, "Пришлите страну проживания", InlineKeyboardBuilder.create()
                .row()
                .button(COUNTRIES.get("Russia"), "Russia")
                .button(COUNTRIES.get("Belarus"), "Belarus")
                .row()
                .button(COUNTRIES.get("Kazakhstan"), "Kazakhstan")
                .button(COUNTRIES.get("China"), "China")
                .build()
        );
        return null;
    }

    private Void stepRegionProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String region = safeGetText(update);

        // Answer
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);

        // Validate
        if (StringUtils.trimToNull(region) == null) {
            messageSender.sendMessage(chatId, "Регион не может быть пустым");
            prevOrFirstStep(update);
            return null;
        }

        // Setting
        history.get(chatId).request.region = region.trim();

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepPhoneNumberRequest(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, "Пришлите номер телефона (необязательно)", InlineKeyboardBuilder.create()
                .row()
                .button("Пропустить", "/skip")
                .build());
        return null;
    }

    private Void stepPhoneNumberProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String phone = safeGetText(update);

        // Validate
        if (StringUtils.trimToNull(phone) == null) {
            messageSender.sendMessage(chatId, "Номер телефона не может быть пустым");
            return null;
        }

        if (phone.length() < 7 || phone.length() > 255) {
            messageSender.sendMessage(chatId, "Номер телефона должен быть от 7 до 255 символов");
            return null;
        }

        if (userRepository.existsUserByPhoneNumber(UserPhoneNumber.of(phone))) {
            messageSender.sendMessage(chatId, "Пользователь с телефоном \"%s\" уже существует".formatted(phone));
            prevOrFirstStep(update);
            return null;
        }

        // Setting
        history.get(chatId).request.phoneNumber = phone.trim();

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepInnRequest(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        RegisterObject registerObject = history.get(chatId);

        if (UserRole.ROLE_USER.equals(registerObject.request.userRole)) {
            nextOrFirstStep(update, 2);
            return null;
        }

        // Act
        messageSender.sendMessage(chatId, "Пришлите ваш ИНН");
        return null;
    }

    private Void stepInnProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String inn = safeGetText(update);
        RegisterObject registerObject = history.get(chatId);

        // Validate
        if (StringUtils.trimToNull(inn) == null) {
            messageSender.sendMessage(chatId, "ИНН не может быть пустым");
            prevOrFirstStep(update);
            return null;
        }

        if (inn.length() < 7 || inn.length() > 255) {
            messageSender.sendMessage(chatId, "ИНН должен содержать от 7 до 255 символов");
            prevOrFirstStep(update);
            return null;
        }

        // Act
        registerObject.request.vendor.inn = inn.trim();

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepVendorCountriesRequest(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        RegisterObject registerObject = history.get(chatId);

        // Check
        if (UserRole.ROLE_USER.equals(registerObject.request.userRole)) {
            nextOrFirstStep(update, 2);
            return null;
        }

        // Act
        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardBuilder.create()
                .row()
                .button("Россия \uD83C\uDDF7\uD83C\uDDFA", "Russia")
                .button("Беларусь \uD83C\uDDE7\uD83C\uDDFE", "Belarus")
                .row()
                .button("Казахстан \uD83C\uDDF0\uD83C\uDDFF", "Kazakhstan")
                .button("Китай \uD83C\uDDE8\uD83C\uDDF3", "China")
                .row()
                .button("Очистить страны", "clear")
                .button("Продолжить регистрацию", "continue")
                .build();

        List<List<InlineKeyboardButton>> keyboard = keyboardMarkup.getKeyboard().stream()
                .map(row -> row.stream()
                        .filter(b -> !registerObject.request.vendor.countries.contains(b.getCallbackData()))
                        .toList()
                )
                .filter(row -> !row.isEmpty())
                .toList();

        keyboardMarkup.setKeyboard(keyboard);

        if (update.getCallbackQuery() != null) {
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(chatId);
            editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageReplyMarkup.setReplyMarkup(keyboardMarkup);

            try {
                messageSender.editMessageReplyMarkup(editMessageReplyMarkup);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            messageSender.sendMessage(chatId, "Добавьте хотя бы одну страну, в которой вы работайте", keyboardMarkup);
        }
        return null;
    }

    private Void stepVendorCountriesProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String commandOrCountry = safeGetText(update);
        RegisterObject registerObject = history.get(chatId);

        // Answer
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);

        // Act
        if ("continue".equals(commandOrCountry)) {
            if (registerObject.request.vendor.countries.isEmpty()) {
                // Prev step
                prevOrFirstStep(update);
                return null;
            }

            // Next step
            nextOrFirstStep(update);
            return null;
        } else if ("clear".equals(commandOrCountry)) {
            registerObject.request.vendor.countries.clear();
            prevOrFirstStep(update);
            return null;
        }

        registerObject.request.vendor.countries.add(commandOrCountry);
        prevOrFirstStep(update);
        return null;
    }

    private Void stepPasswordRequest(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, "Пришлите пароль");
        return null;
    }

    private Void stepPasswordProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String password = safeGetText(update);

        // Hide Password
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(safeGetMessage(update).getMessageId());

        try {
            messageSender.deleteMessage(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }

        // Validate
        if (StringUtils.trimToNull(password) == null) {
            messageSender.sendMessage(chatId, "Пароль не может быть пустым");
            prevOrFirstStep(update);
            return null;
        }

        if (password.length() < 4 || password.length() > 255) {
            messageSender.sendMessage(chatId, "Пароль должен содержать от 4 до 255 символов");
            prevOrFirstStep(update);
            return null;
        }

        // Act
        history.get(chatId).request.password = passwordEncoder.encode(password);

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepConfirmPasswordRequest(Update update) {
        long chatId = safeGetChatId(update);
        messageSender.sendMessage(chatId, "Пришлите пароль еще раз, чтобы подтвердить");
        return null;
    }

    private Void stepConfirmPasswordProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String confirmPassword = safeGetText(update);

        // Hide Password
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(safeGetMessage(update).getMessageId());

        try {
            messageSender.deleteMessage(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }

        // Validate
        if (StringUtils.trimToNull(confirmPassword) == null) {
            messageSender.sendMessage(chatId, "Пароли не совпадают");
            prevOrFirstStep(update, 2);
            return null;
        }

        if (confirmPassword.length() < 4 || confirmPassword.length() > 255) {
            messageSender.sendMessage(chatId, "Пароли не совпадают");
            prevOrFirstStep(update, 2);
            return null;
        }

        if (!passwordEncoder.matches(confirmPassword, history.get(chatId).request.password)) {
            messageSender.sendMessage(chatId, "Пароли не совпадают");
            prevOrFirstStep(update, 2);
            return null;
        }

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepVerifyEmailSendMail(Update update) {
        long chatId = safeGetChatId(update);

        LocalDateTime expiration = LocalDateTime.now().plus(verificationTtl);
        String verificationCode = AuthUtils.generateVerificationCode();

        RegisterObject registerObject = history.get(chatId);
        registerObject.emailVerificationCode = verificationCode;

        messageSender.sendMessage(chatId, """
                Подтверждение почты.
                На почту "%s" был отправлен код подтверждения.
                """.formatted(registerObject.request.email));


        CompletableFuture.runAsync(() -> {
            try {
                mailService.sendVerificationMail(registerObject.request.email, verificationCode, expiration, registerObject.locale);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                messageSender.sendMessage(chatId, "Что-то пошло не так при отправке кода подтверждения на почту");
            }
        });

        nextOrFirstStep(update);
        return null;
    }

    private Void stepVerifyEmailRequest(Update update) {
        long chatId = safeGetChatId(update);
        RegisterObject registerObject = history.get(chatId);

        if (registerObject.emailVerificationAttempts <= 0 || (registerObject.emailVerificationBan != null && registerObject.emailVerificationBan.isAfter(LocalDateTime.now()))) {
            if (registerObject.emailVerificationBan == null) {
                registerObject.emailVerificationBan = LocalDateTime.now().plusMinutes(2);
                registerObject.emailVerificationAttempts = 3;
            }

            Duration duration = Duration.between(LocalDateTime.now(), registerObject.emailVerificationBan);
            long minutes = duration.toMinutes();
            long seconds = duration.toSeconds() % 60;

            messageSender.sendMessage(chatId, """
                    У вас не осталось попыток для подтверждения почты!
                    Попробуйте еще раз через %s мин %s сек
                    """.formatted(minutes, seconds));
            return null;
        }

        messageSender.sendMessage(chatId, "Пришлите код чтобы подтвердить почту");
        return null;
    }

    private Void stepVerifyEmailProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        String verificationCode = safeGetText(update);
        RegisterObject registerObject = history.get(chatId);

        // Validation
        if (registerObject.emailVerificationBan != null && registerObject.emailVerificationBan.isAfter(LocalDateTime.now())) {
            prevOrFirstStep(update);
            return null;
        }

        if (StringUtils.trimToNull(verificationCode) == null) {
            registerObject.emailVerificationAttempts -= registerObject.emailVerificationAttempts <= 0 ? 0 : 1;
            messageSender.sendMessage(chatId, "Неверный код подтверждения (осталось попыток: %s)".formatted(registerObject.emailVerificationAttempts));
            prevOrFirstStep(update);
            return null;
        }

        if (!registerObject.emailVerificationCode.equals(verificationCode)) {
            registerObject.emailVerificationAttempts -= 1;
            messageSender.sendMessage(chatId, "Неверный код подтверждения (осталось попыток: %s)".formatted(registerObject.emailVerificationAttempts));
            prevOrFirstStep(update);
            return null;
        }

        // Act
        registerObject.emailVerificationCode = null;
        messageSender.sendMessage(chatId, "Почта была успешно подтверждена");

        // Next step
        nextOrFirstStep(update);
        return null;
    }

    private Void stepSaveDataRequest(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        RegisterObject registerObject = history.get(chatId);

        // Act
        String message = """
                Все верно?
                Логин: %s,
                Почта: %s,
                Регион: %s,
                Телефон: %s
                """.formatted(
                registerObject.request.login,
                registerObject.request.email,
                registerObject.request.region,
                registerObject.request.phoneNumber != null ? "%s".formatted(registerObject.request.phoneNumber) : "пусто");

        if (UserRole.ROLE_VENDOR.equals(registerObject.request.userRole)) {
            message = """
                    Все верно?
                    Логин: %s,
                    Почта: %s,
                    Телефон: %s,
                    ИНН: %s,
                    Страны: %s
                    """.formatted(
                    registerObject.request.login,
                    registerObject.request.email,
                    registerObject.request.phoneNumber != null ? "%s".formatted(registerObject.request.phoneNumber) : "пусто",
                    registerObject.request.vendor.inn,
                    String.join(", ", registerObject.request.vendor.countries.stream()
                            .map(COUNTRIES::get)
                            .toList()
                    )
            );
        }

        messageSender.sendMessage(chatId, message,
                InlineKeyboardBuilder.create()
                        .row()
                        .button("Начать заново", "/reset")
                        .button("Да, все верно", "/save-data")
                        .build()
        );
        return null;
    }

    private Void stepSaveDataProcess(Update update) {
        // Arrange
        long chatId = safeGetChatId(update);
        RegisterObject registerObject = history.get(chatId);
        RegisterRequest request = registerObject.request;

        // Answer
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
        Message saveMessage = messageSender.sendMessage(chatId, "Сохранение...");
        AtomicBoolean saved = new AtomicBoolean(false);

        CompletableFuture.runAsync(() -> {
            int dots = 1;
            int steps = 0;

            while (!saved.get() && steps < 30) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(saveMessage.getMessageId());
                editMessageText.setText("Сохранение".concat(".".repeat(dots)));

                try {
                    messageSender.editMessageText(editMessageText);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage(), e);
                    saved.set(true);
                    return;
                } finally {
                    steps++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                }

                if (dots < 3) {
                    dots++;
                } else {
                    dots = 1;
                }
            }
        });

        // Act
        var user = new com.surofu.madeinrussia.core.model.user.User();
        UserPassword userPassword = new UserPassword();
        user.setPassword(userPassword);
        userPassword.setUser(user);

        UserPasswordPassword userPasswordPassword = UserPasswordPassword.of(request.password);
        userPassword.setPassword(userPasswordPassword);

        UserLogin userLogin = UserLogin.of(request.login);
        UserEmail userEmail = UserEmail.of(request.email);
        UserPhoneNumber userPhoneNumber = UserPhoneNumber.of(request.phoneNumber);

        if (userRepository.getUserByTelegramUserId(chatId).isPresent()) {
            messageSender.sendMessage(chatId, """
            Что-то пошло не так.
            Аккаунт Exporteru.com уже привязан к этому пользователю Telegram
            """);
            return null;
        }

        user.setRole(request.userRole);
        user.setTelegramUserId(chatId);
        user.setLogin(userLogin);
        user.setEmail(userEmail);
        user.setPhoneNumber(userPhoneNumber);

        if (UserRole.ROLE_VENDOR.equals(request.userRole)) {
            VendorDetails vendorDetails = new VendorDetails();
            vendorDetails.setUser(user);
            user.setVendorDetails(vendorDetails);
            VendorDetailsInn inn = VendorDetailsInn.of(request.vendor.inn);
            vendorDetails.setInn(inn);

            for (String countryName : request.vendor.countries) {
                VendorCountry vendorCountry = new VendorCountry();
                vendorCountry.setVendorDetails(vendorDetails);
                vendorCountry.setName(VendorCountryName.of(countryName));

                try {
                    HstoreTranslationDto translationDto = translationRepository.expand(countryName);
                    vendorCountry.getName().setTranslations(translationDto);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    messageSender.sendMessage(chatId, "Что-то пошло е так...");
                    return null;
                }

                vendorDetails.getVendorCountries().add(vendorCountry);
            }

            user.setRegion(UserRegion.of(request.vendor.countries.iterator().next()));
        } else {
            UserRegion userRegion = UserRegion.of(request.region);
            user.setRegion(userRegion);
        }

        Session session = Session.of(registerObject.sessionInfo, user, new Session());
        session.setUser(user);
        user.getSessions().add(session);

        try {
            userRepository.save(user);

            if (user.getVendorDetails() != null) {
                vendorCountryRepository.saveAll(user.getVendorDetails().getVendorCountries());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            messageSender.sendMessage(chatId, "Что-то пошло не так...");
            return null;
        } finally {
            saved.set(true);

            // Hide Password
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(saveMessage.getMessageId());

            try {
                messageSender.deleteMessage(deleteMessage);
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        }

        SecurityUser securityUser = new SecurityUser(user, userPassword, null);
        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        String rawUrl = "%s?accessToken=%s&refreshToken=%s".formatted(
                redirectSuccessHost, accessToken, refreshToken
        );
        String formatedUrl = UriComponentsBuilder.fromUriString(rawUrl)
                .build()
                .encode()
                .toUriString();

        messageSender.sendMessage(chatId, """
                Регистрация прошла успешно!
                """, InlineKeyboardBuilder.create()
                .row()
                .urlButton("Вернуться на Exporteru.com", formatedUrl)
                .build());

        // Reset
        clearSteps(update);
        return null;
    }

    // ---------- Utils ---------- //
    private void nextOrFirstStep(Update update) {
        nextOrFirstStep(update, 1);
    }

    private void nextOrFirstStep(Update update, int count) {
        long chatId = safeGetChatId(update);

        if (!history.containsKey(chatId)) {
            help(update);
            return;
        }

        RegisterObject registerObject = history.get(chatId);

        for (int i = 0; i < count; i++) {
            registerObject.step = registerObject.step.next();
        }

        stepFunctions.get(registerObject.step.index).apply(update);
    }

    private void prevOrFirstStep(Update update) {
        prevOrFirstStep(update, 1);
    }

    private void prevOrFirstStep(Update update, int count) {
        long chatId = safeGetChatId(update);


        if (!history.containsKey(chatId)) {
            help(update);
            return;
        }

        RegisterObject registerObject = history.get(chatId);

        for (int i = 0; i < count; i++) {
            registerObject.step = registerObject.step.prev();
        }

        stepFunctions.get(registerObject.step.index).apply(update);
    }

    @Transactional(readOnly = true)
    protected boolean userExistsByEmail(String email) {
        return userRepository.existsUserByEmail(UserEmail.of(email));
    }

    public Void cancel(Update update) {
        clearSteps(update);
        long chatId = safeGetChatId(update);
        User user = safeGetUser(update);
        String greeting = """
                Привет, %s! Добро пожаловать на Exporteru.com!
                Нажмите на кнопку "Зарегистрироваться", чтобы начать регистрацию
                """.formatted(user.getFirstName());

        InlineKeyboardMarkup markup = InlineKeyboardBuilder.create()
                .row()
                .button("Зарегистрироваться", "/register")
                .build();

        messageSender.sendMessage(chatId, greeting, markup);
        return null;
    }

    private enum RegisterStep {
        BEGIN(0),
        ACCOUNT_TYPE_REQUEST(1),
        ACCOUNT_TYPE_PROCESS(2),
        LOGIN_REQUEST(3),
        LOGIN_PROCESS(4),
        EMAIL_REQUEST(5),
        EMAIL_PROCESS(6),
        REGION_REQUEST(7),
        REGION_PROCESS(8),
        PHONE_NUMBER_REQUEST(9),
        PHONE_NUMBER_PROCESS(10),
        INN_REQUEST_REQUEST(11),
        INN_REQUEST_PROCESS(12),
        VENDOR_COUNTRIES_REQUEST(13),
        VENDOR_COUNTRIES_PROCESS(14),
        PASSWORD_REQUEST(15),
        PASSWORD_PROCESS(16),
        CONFIRM_PASSWORD_REQUEST(17),
        CONFIRM_PASSWORD_PROCESS(18),
        VERIFY_EMAIL_SEND_MAIL(19),
        VERIFY_EMAIL_REQUEST(20),
        VERIFY_EMAIL_PROCESS(21),
        SAVE_DATA_REQUEST(22),
        SAVE_DATA_PROCESS(23);

        private final int index;

        RegisterStep(int index) {
            this.index = index;
        }

        public static RegisterStep valueOf(int index) {
            for (RegisterStep step : RegisterStep.values()) {
                if (step.index == index) {
                    return step;
                }
            }
            throw new IllegalArgumentException("Unknown register step index: " + index);
        }

        public RegisterStep next() {
            return RegisterStep.valueOf(this.index + 1);
        }

        public RegisterStep prev() {
            return RegisterStep.valueOf(this.index - 1);
        }
    }

    @AllArgsConstructor
    private static class RegisterObject {
        private RegisterStep step;
        private RegisterRequest request;
        private SessionInfo sessionInfo;
        private Locale locale;
        private String emailVerificationCode;
        private int emailVerificationAttempts;
        private LocalDateTime emailVerificationBan;

        public RegisterObject(RegisterStep step, RegisterRequest request, SessionInfo sessionInfo, Locale locale) {
            this.step = step;
            this.request = request;
            this.sessionInfo = sessionInfo;
            this.locale = locale;
            this.emailVerificationCode = null;
            this.emailVerificationAttempts = 3;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class RegisterRequest {
        private UserRole userRole = UserRole.ROLE_USER;
        private String email;
        private String login;
        private String password;
        private String region;
        private String phoneNumber;
        private VendorRequest vendor = new VendorRequest();

        @NoArgsConstructor
        @AllArgsConstructor
        public static class VendorRequest {
            private String inn;
            private List<String> countries = new ArrayList<>();
        }
    }




}
