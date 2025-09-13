package com.surofu.madeinrussia.application.components.telegrambot;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SessionRepository sessionRepository;

    @Value("${app.frontend.oauth.telegram.redirect.success}")
    private String redirectHost;

    @Transactional
    public void processLogin(Update update, SessionInfo sessionInfo, Locale locale) {
        long chatId = TelegramBotUtils.safeGetChatId(update);
        Optional<User> userOptional = userRepository.getUserByTelegramUserId(chatId);

        if (userOptional.isEmpty()) {
            messageSender.sendMessage(chatId, """
                    Что-то пошло не так.
                    Пользователь Exporteru.com не привязан к этой учетной записи Телеграм
                    """);
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
            messageSender.sendMessage(chatId, "Что-то пошло не так...");
            return;
        }

        String rawUrl = "%s?accessToken=%s&refreshToken=%s".formatted(redirectHost, accessToken, refreshToken);
        String url = UriComponentsBuilder.fromUriString(rawUrl)
                .build()
                .encode()
                .toUriString();

        messageSender.sendMessage(chatId, "Вход успешно выполнен!", InlineKeyboardBuilder.create()
                .row()
                .urlButton("Вернуться на Exporteru.com", url)
                .build());

    }
}
