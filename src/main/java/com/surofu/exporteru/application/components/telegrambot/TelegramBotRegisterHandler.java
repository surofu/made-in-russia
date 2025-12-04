package com.surofu.exporteru.application.components.telegrambot;

import static com.surofu.exporteru.application.components.telegrambot.TelegramBotUtils.safeGetChatId;
import static com.surofu.exporteru.application.components.telegrambot.TelegramBotUtils.safeGetMessage;
import static com.surofu.exporteru.application.components.telegrambot.TelegramBotUtils.safeGetText;
import static com.surofu.exporteru.application.components.telegrambot.TelegramBotUtils.safeGetUser;
import static com.surofu.exporteru.application.components.telegrambot.TelegramBotUtils.saveGetCallbackId;

import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.application.utils.AuthUtils;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.telegram.TelegramUser;
import com.surofu.exporteru.core.model.user.UserAvatar;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.model.user.UserRegion;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.model.user.password.UserPassword;
import com.surofu.exporteru.core.model.user.password.UserPasswordPassword;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsAddress;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.core.repository.VendorDetailsRepository;
import com.surofu.exporteru.core.service.mail.MailService;
import com.surofu.exporteru.infrastructure.persistence.vendor.country.JpaVendorCountryRepository;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
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

@Slf4j
@Component
public class TelegramBotRegisterHandler {
  private final Map<Long, RegisterObject> history = new ConcurrentHashMap<>();
  private final UserRepository userRepository;
  private final Pattern EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;
  private final JwtUtils jwtUtils;
  private final Map<String, String> COUNTRIES = new ConcurrentHashMap<>();
  private final TranslationRepository translationRepository;
  private final JpaVendorCountryRepository vendorCountryRepository;
  private final KeyboardBuilder keyboardBuilder;
  private final LocalizationManager localizationManager;
  private final VendorDetailsRepository vendorDetailsRepository;
  private MessageSender messageSender;
  @Value("${app.redis.verification-ttl-duration}")
  private Duration verificationTtl;
  @Value("${app.frontend.oauth.telegram.redirect.success}")
  private String redirectSuccessHost;

  public TelegramBotRegisterHandler(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      MailService mailService,
      JwtUtils jwtUtils,
      TranslationRepository translationRepository,
      JpaVendorCountryRepository vendorCountryRepository,
      KeyboardBuilder keyboardBuilder,
      LocalizationManager localizationManager,
      VendorDetailsRepository vendorDetailsRepository,
      @Value("${app.redis.verification-ttl-duration}") Duration verificationTtl,
      @Value("${app.frontend.oauth.telegram.redirect.success}") String redirectSuccessHost) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.mailService = mailService;
    this.jwtUtils = jwtUtils;
    this.translationRepository = translationRepository;
    this.vendorCountryRepository = vendorCountryRepository;
    this.keyboardBuilder = keyboardBuilder;
    this.localizationManager = localizationManager;
    this.vendorDetailsRepository = vendorDetailsRepository;
    this.verificationTtl = verificationTtl;
    this.redirectSuccessHost = redirectSuccessHost;
  }

  @Autowired
  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  @PostConstruct
  public void init() {
    String russia = localizationManager.localize("telegram.bot.country.russia");
    String belarus = localizationManager.localize("telegram.bot.country.belarus");
    String kazakhstan = localizationManager.localize("telegram.bot.country.kazakhstan");
    String china = localizationManager.localize("telegram.bot.country.china");

    COUNTRIES.putAll(Map.of(
        "Russia", russia + " \uD83C\uDDF7\uD83C\uDDFA",
        "Belarus", belarus + " \uD83C\uDDE7\uD83C\uDDFE",
        "Kazakhstan", kazakhstan + " \uD83C\uDDF0\uD83C\uDDFF",
        "China", china + " \uD83C\uDDE8\uD83C\uDDF3"
    ));
  }

  public void setup(TelegramUser telegramUser, SessionInfo sessionInfo, Locale locale) {
    RegisterRequest request = new RegisterRequest();
    request.avatarUrl = telegramUser.photoUrl();

    history.put(telegramUser.id(), new RegisterObject(
        RegisterStep.BEGIN,
        request,
        sessionInfo,
        locale
    ));
  }  private final List<Function<Update, Void>> stepFunctions = List.of(
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
      this::stepAddressRequest,
      this::stepAddressProcess,
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

  public void setLocale(Update update, Locale locale) {
    long chatId = TelegramBotUtils.safeGetChatId(update);
    RegisterObject registerObject = history.get(chatId);
    registerObject.locale = locale;
  }

  public void error(Update update) {
    long chatId = safeGetChatId(update);
    String text = localizationManager.localize("telegram.bot.error.handle.response");
    String again = localizationManager.localize("telegram.bot.markup.start-again");
    messageSender.sendMessage(chatId, text, InlineKeyboardBuilder.create()
        .row()
        .button(again, "/reset")
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
    String text = localizationManager.localize("telegram.bot.error.help");
    messageSender.sendMessage(chatId, text, InlineKeyboardBuilder.create()
        .row()
        .urlButton("Exporteru.com", "https://exporteru.com/login")
        .build());
  }

  public Void processRegister(Update update) {
    // Answer
    try {
      String callbackId = saveGetCallbackId(update);
      messageSender.answerCallback(callbackId, "", false);
    } catch (Exception ignored) {
    }

    long chatId = safeGetChatId(update);

    if (history.containsKey(chatId)) {
      RegisterObject registerObject = history.get(chatId);
      LocaleContextHolder.setLocale(registerObject.locale);

    }

    nextOrFirstStep(update);
    return null;
  }

  private Void stepBegin(Update update) {
    long chatId = safeGetChatId(update);
    String text = localizationManager.localize("telegram.bot.register.begin");
    messageSender.sendMessage(chatId, text, keyboardBuilder.createMainMenu());

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
    } catch (Exception ignored) {
    }

    String requestText =
        localizationManager.localize("telegram.bot.register.step.account-type.request");
    String markupBuyer = localizationManager.localize("telegram.bot.register.markup.buyer");
    String markupVendor = localizationManager.localize("telegram.bot.register.markup.vendor");

    messageSender.sendMessage(chatId, requestText, InlineKeyboardBuilder.create()
        .row()
        .button(markupBuyer, "buyer")
        .button(markupVendor, "vendor")
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
    } else if ("buyer".equals(accountType)) {
      registerObject.request.userRole = UserRole.ROLE_USER;
    } else {
      return null;
    }

    // Next step
    nextOrFirstStep(update);
    return null;
  }

  private Void stepLoginRequest(Update update) {
    long chatId = safeGetChatId(update);
    String requestText = localizationManager.localize("telegram.bot.register.step.login.request");
    messageSender.sendMessage(chatId, requestText);
    return null;
  }

  private Void stepLoginProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String login = safeGetText(update);

    if (!update.hasMessage()) {
      if (update.hasCallbackQuery()) {
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
      }

      return null;
    }

    // Validate
    if (StringUtils.trimToNull(login) == null) {
      String text = localizationManager.localize("telegram.bot.validation.login.empty");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    if (login.length() < 2 || login.length() > 255) {
      String text = localizationManager.localize("telegram.bot.validation.login.length");
      messageSender.sendMessage(chatId, text);
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
    String requestText = localizationManager.localize("telegram.bot.register.step.email.request");
    messageSender.sendMessage(chatId, requestText);
    return null;
  }

  @Transactional(readOnly = true)
  protected Void stepEmailProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String email = safeGetText(update).trim().toLowerCase();

    if (!update.hasMessage()) {
      if (update.hasCallbackQuery()) {
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
      }

      return null;
    }

    // Validate
    if (StringUtils.trimToNull(email) == null) {
      String text = localizationManager.localize("telegram.bot.validation.email.empty");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    if (email.length() < 2 || email.length() > 255) {
      String text = localizationManager.localize("telegram.bot.validation.email.length");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    if (!EMAIL_PATTERN.matcher(email).matches()) {
      String text = localizationManager.localize("telegram.bot.validation.email.invalid");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    if (userExistsByEmail(email)) {
      String text =
          localizationManager.localize("telegram.bot.validation.email.already-exists", email);
      messageSender.sendMessage(chatId, text);
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

    String requestText = localizationManager.localize("telegram.bot.register.step.region.request");

    messageSender.sendMessage(chatId, requestText, InlineKeyboardBuilder.create()
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

    if (!COUNTRIES.containsKey(region)) {
      String text =
          localizationManager.localize("telegram.bot.validation.region.not-found", region);
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    // Answer
    String callbackId = saveGetCallbackId(update);
    messageSender.answerCallback(callbackId, "", false);

    // Validate
    if (StringUtils.trimToNull(region) == null) {
      String text = localizationManager.localize("telegram.bot.validation.region.empty");
      messageSender.sendMessage(chatId, text);
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
    String requestText = localizationManager.localize("telegram.bot.register.step.phone.request");
    String skipText = localizationManager.localize("telegram.bot.markup.skip");
    messageSender.sendMessage(chatId, requestText, InlineKeyboardBuilder.create()
        .row()
        .button(skipText, "/skip")
        .build());
    return null;
  }

  private Void stepPhoneNumberProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String phone = safeGetText(update);

    if (!update.hasMessage()) {
      if (update.hasCallbackQuery()) {
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
      }

      return null;
    }

    // Validate
    if (StringUtils.trimToNull(phone) == null) {
      String text = localizationManager.localize("telegram.bot.validation.phone.empty");
      messageSender.sendMessage(chatId, text);
      return null;
    }

    if (phone.length() < 7 || phone.length() > 255) {
      String text = localizationManager.localize("telegram.bot.validation.phone.length");
      messageSender.sendMessage(chatId, text);
      return null;
    }

    if (userRepository.existsUserByPhoneNumber(UserPhoneNumber.of(phone))) {
      String text =
          localizationManager.localize("telegram.bot.validation.phone.already-exists", phone);
      messageSender.sendMessage(chatId, text);
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
    String requestText = localizationManager.localize("telegram.bot.register.step.inn.request");
    messageSender.sendMessage(chatId, requestText);
    return null;
  }

  private Void stepInnProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String inn = safeGetText(update);
    RegisterObject registerObject = history.get(chatId);

    if (!update.hasMessage()) {
      if (update.hasCallbackQuery()) {
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
      }

      return null;
    }

    // Validate
    if (StringUtils.trimToNull(inn) == null) {
      String text = localizationManager.localize("telegram.bot.validation.inn.empty");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    if (inn.length() < 7 || inn.length() > 255) {
      String text = localizationManager.localize("telegram.bot.validation.inn.length");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    if (vendorDetailsRepository.existsByInn(VendorDetailsInn.of(inn))) {
      String text = localizationManager.localize("telegram.bot.validation.inn.already-exists", inn);
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    // Act
    registerObject.request.vendor.inn = inn.trim();

    // Next step
    nextOrFirstStep(update);
    return null;
  }

  private Void stepAddressRequest(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    RegisterObject registerObject = history.get(chatId);

    if (UserRole.ROLE_USER.equals(registerObject.request.userRole)) {
      nextOrFirstStep(update, 2);
      return null;
    }

    // Act
    String requestText = localizationManager.localize("telegram.bot.register.step.address.request");
    String skipText = localizationManager.localize("telegram.bot.markup.skip");
    messageSender.sendMessage(chatId, requestText, InlineKeyboardBuilder.create()
        .row()
        .button(skipText, "/skip")
        .build()
    );
    return null;
  }

  private Void stepAddressProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String address = safeGetText(update);
    RegisterObject registerObject = history.get(chatId);

    if (!update.hasMessage()) {
      if (update.hasCallbackQuery()) {
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
      }

      return null;
    }

    // Validate
    if (StringUtils.trimToNull(address) == null) {
      String text = localizationManager.localize("telegram.bot.validation.address.empty");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    // Act
    registerObject.request.vendor.address = address.trim();

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
    String markupClear = localizationManager.localize("telegram.bot.country.markup.clear");
    String markupContinue = localizationManager.localize("telegram.bot.register.markup.continue");


    InlineKeyboardMarkup keyboardMarkup = InlineKeyboardBuilder.create()
        .row()
        .button(COUNTRIES.get("Russia") + " \uD83C\uDDF7\uD83C\uDDFA", "Russia")
        .button(COUNTRIES.get("Belarus") + " \uD83C\uDDE7\uD83C\uDDFE", "Belarus")
        .row()
        .button(COUNTRIES.get("Kazakhstan") + " \uD83C\uDDF0\uD83C\uDDFF", "Kazakhstan")
        .button(COUNTRIES.get("China") + " \uD83C\uDDE8\uD83C\uDDF3", "China")
        .row()
        .button(markupClear, "clear")
        .button(markupContinue, "continue")
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
      String text = localizationManager.localize("telegram.bot.validation.countries.empty");
      messageSender.sendMessage(chatId, text, keyboardMarkup);
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
    }

    if ("clear".equals(commandOrCountry)) {
      registerObject.request.vendor.countries.clear();
      prevOrFirstStep(update);
      return null;
    }

    if (!COUNTRIES.containsKey(commandOrCountry)) {
      String text = localizationManager.localize("telegram.bot.validation.region.not-found",
          commandOrCountry);
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    registerObject.request.vendor.countries.add(commandOrCountry);
    prevOrFirstStep(update);
    return null;
  }

  private Void stepPasswordRequest(Update update) {
    long chatId = safeGetChatId(update);
    String text = localizationManager.localize("telegram.bot.register.step.password.request");
    messageSender.sendMessage(chatId, text);
    return null;
  }

  private Void stepPasswordProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String password = safeGetText(update);

    if (!update.hasMessage()) {
      if (update.hasCallbackQuery()) {
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
      }

      return null;
    }

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
      String text = localizationManager.localize("telegram.bot.validation.password.empty");
      messageSender.sendMessage(chatId, text);
      prevOrFirstStep(update);
      return null;
    }

    if (password.length() < 4 || password.length() > 255) {
      String text = localizationManager.localize("telegram.bot.validation.password.length");
      messageSender.sendMessage(chatId, text);
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
    String text =
        localizationManager.localize("telegram.bot.register.step.password-confirmation.request");
    messageSender.sendMessage(chatId, text);
    return null;
  }

  private Void stepConfirmPasswordProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String confirmPassword = safeGetText(update);

    if (!update.hasMessage()) {
      if (update.hasCallbackQuery()) {
        String callbackId = saveGetCallbackId(update);
        messageSender.answerCallback(callbackId, "", false);
      }

      return null;
    }

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
    String notMatchText =
        localizationManager.localize("telegram.bot.validation.password.not-match");

    if (StringUtils.trimToNull(confirmPassword) == null) {
      messageSender.sendMessage(chatId, notMatchText);
      prevOrFirstStep(update, 2);
      return null;
    }

    if (confirmPassword.length() < 4 || confirmPassword.length() > 255) {
      messageSender.sendMessage(chatId, notMatchText);
      prevOrFirstStep(update, 2);
      return null;
    }

    if (!passwordEncoder.matches(confirmPassword, history.get(chatId).request.password)) {
      messageSender.sendMessage(chatId, notMatchText);
      prevOrFirstStep(update, 2);
      return null;
    }

    // Next step
    nextOrFirstStep(update);
    return null;
  }

  private Void stepVerifyEmailSendMail(Update update) {
    long chatId = safeGetChatId(update);

    ZonedDateTime expiration = ZonedDateTime.now().plus(verificationTtl);
    String verificationCode = AuthUtils.generateVerificationCode();

    RegisterObject registerObject = history.get(chatId);
    registerObject.emailVerificationCode = verificationCode;

    String requestText =
        localizationManager.localize("telegram.bot.register.step.email-confirmation.notification",
            registerObject.request.email);
    messageSender.sendMessage(chatId, requestText);


    CompletableFuture.runAsync(() -> {
      try {
        mailService.sendVerificationMail(registerObject.request.email, verificationCode, expiration,
            registerObject.locale);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        String text = localizationManager.localize("telegram.bot.error.email-confirmation.unknown");
        messageSender.sendMessage(chatId, text);
      }
    });

    nextOrFirstStep(update);
    return null;
  }

  private Void stepVerifyEmailRequest(Update update) {
    long chatId = safeGetChatId(update);
    RegisterObject registerObject = history.get(chatId);

    if (registerObject.emailVerificationAttempts <= 0 ||
        (registerObject.emailVerificationBan != null &&
            registerObject.emailVerificationBan.isAfter(ZonedDateTime.now()))) {
      if (registerObject.emailVerificationBan == null) {
        registerObject.emailVerificationBan = ZonedDateTime.now().plusMinutes(2);
        registerObject.emailVerificationAttempts = 3;
      }

      Duration duration =
          Duration.between(ZonedDateTime.now(), registerObject.emailVerificationBan);
      long minutes = duration.toMinutes();
      long seconds = duration.toSeconds() % 60;

      String text =
          localizationManager.localize("telegram.bot.validation.email-confirmation.no-retries",
              minutes, seconds);
      messageSender.sendMessage(chatId, text);
      return null;
    }

    String requestText =
        localizationManager.localize("telegram.bot.register.step.email-confirmation.request");
    messageSender.sendMessage(chatId, requestText);
    return null;
  }

  private Void stepVerifyEmailProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    String verificationCode = safeGetText(update);
    RegisterObject registerObject = history.get(chatId);

    // Validation
    if (registerObject.emailVerificationBan != null &&
        registerObject.emailVerificationBan.isAfter(ZonedDateTime.now())) {
      prevOrFirstStep(update);
      return null;
    }

    String invalidCodeText =
        localizationManager.localize("telegram.bot.validation.email-confirmation.invalid",
            registerObject.emailVerificationAttempts);

    if (StringUtils.trimToNull(verificationCode) == null) {
      registerObject.emailVerificationAttempts -=
          registerObject.emailVerificationAttempts <= 0 ? 0 : 1;
      messageSender.sendMessage(chatId, invalidCodeText);
      prevOrFirstStep(update);
      return null;
    }

    if (!registerObject.emailVerificationCode.equals(verificationCode)) {
      registerObject.emailVerificationAttempts -= 1;
      messageSender.sendMessage(chatId, invalidCodeText);
      prevOrFirstStep(update);
      return null;
    }

    // Act
    registerObject.emailVerificationCode = null;
    String successText =
        localizationManager.localize("telegram.bot.register.step.email-confirmation.success");
    messageSender.sendMessage(chatId, successText);

    // Next step
    nextOrFirstStep(update);
    return null;
  }

  private Void stepSaveDataRequest(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    RegisterObject registerObject = history.get(chatId);

    // Act
    String requestUserText = localizationManager.localize(
        "telegram.bot.register.step.save-data.request-user",
        registerObject.request.login,
        registerObject.request.email,
        registerObject.request.region,
        registerObject.request.phoneNumber != null ?
            "%s".formatted(registerObject.request.phoneNumber) : "пусто");

    if (UserRole.ROLE_VENDOR.equals(registerObject.request.userRole)) {
      requestUserText = localizationManager.localize(
          "telegram.bot.register.step.save-data.request-vendor",
          registerObject.request.login,
          registerObject.request.email,
          registerObject.request.phoneNumber != null ?
              "%s".formatted(registerObject.request.phoneNumber) : "пусто",
          registerObject.request.vendor.inn,
          String.join(", ", registerObject.request.vendor.countries.stream()
              .map(COUNTRIES::get)
              .toList()
          )
      );
    }

    String markupAgainText =
        localizationManager.localize("telegram.bot.register.step.save-data.markup.again");
    String markupAgreeText =
        localizationManager.localize("telegram.bot.register.step.save-data.markup.agree");

    messageSender.sendMessage(chatId, requestUserText,
        InlineKeyboardBuilder.create()
            .row()
            .button(markupAgainText, "/reset")
            .button(markupAgreeText, "/save-data")
            .build()
    );
    return null;
  }

  @Transactional
  protected Void stepSaveDataProcess(Update update) {
    // Arrange
    long chatId = safeGetChatId(update);
    RegisterObject registerObject = history.get(chatId);
    RegisterRequest request = registerObject.request;

    // Answer
    String callbackId = saveGetCallbackId(update);
    messageSender.answerCallback(callbackId, "", false);
    Message saveMessage = messageSender.sendMessage(chatId, "Сохранение...");
    AtomicBoolean saved = new AtomicBoolean(false);

    String saveProcessText =
        localizationManager.localize("telegram.bot.register.step.save-data.process");

    CompletableFuture.runAsync(() -> {
      int dots = 1;
      int steps = 0;

      while (!saved.get() && steps < 30) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(saveMessage.getMessageId());
        editMessageText.setText(saveProcessText.concat(".".repeat(dots)));

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
    var user = new com.surofu.exporteru.core.model.user.User();
    UserPassword userPassword = new UserPassword();
    user.setPassword(userPassword);
    userPassword.setUser(user);

    UserPasswordPassword userPasswordPassword = UserPasswordPassword.of(request.password);
    userPassword.setPassword(userPasswordPassword);

    UserEmail userEmail = UserEmail.of(request.email);
    UserPhoneNumber userPhoneNumber = UserPhoneNumber.of(request.phoneNumber);

    if (userRepository.getUserByTelegramUserId(chatId).isPresent()) {
      String text = localizationManager.localize("telegram.bot.error.register.already-linked");
      messageSender.sendMessage(chatId, text);
      return null;
    }

    user.setRole(request.userRole);
    user.setTelegramUserId(chatId);
    user.setLogin(TransliterationManager.transliterateUserLogin(new UserLogin(request.login), registerObject.locale));
    user.setEmail(userEmail);
    user.setAvatar(UserAvatar.of(StringUtils.trimToNull(request.avatarUrl)));
    user.setPhoneNumber(userPhoneNumber);

    if (UserRole.ROLE_VENDOR.equals(request.userRole)) {
      VendorDetails vendorDetails = new VendorDetails();
      vendorDetails.setUser(user);
      user.setVendorDetails(vendorDetails);
      VendorDetailsInn inn = VendorDetailsInn.of(request.vendor.inn);
      vendorDetails.setInn(inn);
      VendorDetailsAddress address = VendorDetailsAddress.of(request.vendor.address);
      vendorDetails.setAddress(address);

      for (String countryName : request.vendor.countries) {
        VendorCountry vendorCountry = new VendorCountry();
        vendorCountry.setVendorDetails(vendorDetails);
        vendorCountry.setName(VendorCountryName.of(countryName));

        try {
          vendorCountry.getName().setTranslations(translationRepository.expand(countryName));
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          String text = localizationManager.localize("telegram.bot.error.unknown");
          messageSender.sendMessage(chatId, text);
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
      String text = localizationManager.localize("telegram.bot.error.unknown");
      messageSender.sendMessage(chatId, text);
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

    String successText = localizationManager.localize("telegram.bot.register.success");
    messageSender.sendMessage(chatId, successText, InlineKeyboardBuilder.create()
        .row()
        .urlButton("Exporteru.com", formatedUrl)
        .build());

    history.remove(chatId);
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
    String greeting =
        localizationManager.localize("telegram.bot.register.start", user.getFirstName());
    String markupBegin = localizationManager.localize("telegram.bot.register.markup.begin");
    InlineKeyboardMarkup markup = InlineKeyboardBuilder.create()
        .row()
        .button(markupBegin, "/register")
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
    ADDRESS_REQUEST_REQUEST(13),
    ADDRESS_REQUEST_PROCESS(14),
    VENDOR_COUNTRIES_REQUEST(15),
    VENDOR_COUNTRIES_PROCESS(16),
    PASSWORD_REQUEST(17),
    PASSWORD_PROCESS(18),
    CONFIRM_PASSWORD_REQUEST(19),
    CONFIRM_PASSWORD_PROCESS(20),
    VERIFY_EMAIL_SEND_MAIL(21),
    VERIFY_EMAIL_REQUEST(22),
    VERIFY_EMAIL_PROCESS(23),
    SAVE_DATA_REQUEST(24),
    SAVE_DATA_PROCESS(25);

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
    private ZonedDateTime emailVerificationBan;

    public RegisterObject(RegisterStep step, RegisterRequest request, SessionInfo sessionInfo,
                          Locale locale) {
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
    private final VendorRequest vendor = new VendorRequest();
    private UserRole userRole = UserRole.ROLE_USER;
    private String email;
    private String login;
    private String password;
    private String region;
    private String phoneNumber;
    private String avatarUrl;

    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorRequest {
      private final List<String> countries = new ArrayList<>();
      private String inn;
      private String address;
    }
  }



}
