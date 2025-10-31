package com.surofu.exporteru.application.components.telegrambot;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.session.SessionDeviceId;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.repository.SessionRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotLoginHandler {
    private final MessageSender messageSender;
    private final UserRepository userRepository;
    private final LocalizationManager localizationManager;
    private final JwtUtils jwtUtils;
    private final SessionRepository sessionRepository;

    @Value("${app.frontend.oauth.telegram.redirect.success}")
    private String redirectHost;

    @Transactional
    public void processLogin(Update update, SessionInfo sessionInfo, Locale locale) {
        LocaleContextHolder.setLocale(locale);
        long chatId = TelegramBotUtils.safeGetChatId(update);
        Optional<User> userOptional = userRepository.getUserByTelegramUserId(chatId);

        if (userOptional.isEmpty()) {
            String text = localizationManager.localize("telegram.bot.error.login.not-linked");
            messageSender.sendMessage(chatId, text);
            return;
        }

        User user = userOptional.get();
        SecurityUser securityUser = new SecurityUser(user, user.getPassword(), sessionInfo);

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
        Session oldSession = sessionRepository
                .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
                .orElse(new Session());
        Session session = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);

        try {
            sessionRepository.save(session);
        } catch (Exception e) {
            log.error("Save session error: {}", e.getMessage());
            String text = localizationManager.localize("telegram.bot.error.unknown");
            messageSender.sendMessage(chatId, text);
            return;
        }

        String rawUrl = "%s?accessToken=%s&refreshToken=%s".formatted(redirectHost, accessToken, refreshToken);
        String url = UriComponentsBuilder.fromUriString(rawUrl)
                .build()
                .encode()
                .toUriString();

        String successLoginText = localizationManager.localize("telegram.bot.login.success");
        String markupRedirect = localizationManager.localize("telegram.bot.login.markup.redirect");
        messageSender.sendMessage(chatId, successLoginText, InlineKeyboardBuilder.create()
                .row()
                .urlButton(markupRedirect, url)
                .build());

    }
}
