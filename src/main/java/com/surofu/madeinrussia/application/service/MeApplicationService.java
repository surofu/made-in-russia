package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.TokenDto;
import com.surofu.madeinrussia.application.dto.SessionDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.model.SecurityUser;
import com.surofu.madeinrussia.application.model.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.session.SessionWithUser;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.repository.SessionWithUserRepository;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.GetMe;
import com.surofu.madeinrussia.core.service.me.operation.GetMeCurrentSession;
import com.surofu.madeinrussia.core.service.me.operation.GetMeSessions;
import com.surofu.madeinrussia.core.service.me.operation.RefreshMeCurrentSession;
import com.surofu.madeinrussia.core.service.user.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeApplicationService implements MeService {
    private final SessionRepository sessionRepository;
    private final SessionWithUserRepository sessionWithUserRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    private final AsyncSessionApplicationService asyncSessionApplicationService;

    @Override
    @Transactional(readOnly = true)
    public GetMe.Result getMeByJwt(GetMe operation) {
        Optional<Session> existingSession = getSessionBySecurityUser(operation.getQuery().securityUser());

        if (existingSession.isEmpty()) {
            return GetMe.Result.sessionWithUserIdAndDeviceIdNotFound(
                    operation.getQuery().securityUser().getUser().getId(),
                    operation.getQuery().securityUser().getSessionInfo().getDeviceId()
            );
        }

        Optional<SessionWithUser> sessionWithUser = sessionWithUserRepository.getSessionById(existingSession.get().getId());

        if (sessionWithUser.isEmpty()) {
            return GetMe.Result.sessionWithIdNotFound(existingSession.get().getId());
        }

        User user = sessionWithUser.get().getUser();
        UserDto userDto = UserDto.of(user);

        return GetMe.Result.success(userDto);
    }

    @Override
    public GetMeSessions.Result getMeSessions(GetMeSessions operation) {
        SecurityUser securityUser = operation.getQuery().securityUser();

        User user = securityUser.getUser();
        Long userId = user.getId();

        List<SessionDto> sessionDtos = sessionRepository.getSessionsByUserId(userId).stream()
                .map(SessionDto::of)
                .toList();

        return GetMeSessions.Result.success(sessionDtos);
    }

    @Override
    public GetMeCurrentSession.Result getMeCurrentSession(GetMeCurrentSession operation) {
        Long userId = operation.getQuery().securityUser().getUser().getId();
        String rawDeviceId = operation.getQuery().securityUser().getSessionInfo().getDeviceId();
        SessionDeviceId sessionDeviceId = SessionDeviceId.of(rawDeviceId);

        Optional<SessionDto> sessionDto = sessionRepository
                .getSessionByUserIdAndDeviceId(userId, sessionDeviceId)
                .map(SessionDto::of);

        if (sessionDto.isEmpty()) {
            return GetMeCurrentSession.Result.sessionNotFound(userId, sessionDeviceId);
        }

        return GetMeCurrentSession.Result.success(sessionDto.get());
    }

    @Override
    public RefreshMeCurrentSession.Result refreshMeCurrentSession(RefreshMeCurrentSession operation) {
        String refreshToken = operation.getCommand().refreshToken();

        String rawEmail;

        try {
            rawEmail = jwtUtils.extractEmailFromRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException ex) {
            return RefreshMeCurrentSession.Result.invalidRefreshToken(refreshToken, ex);
        }

        SecurityUser securityUser;

        try {
            securityUser = (SecurityUser) userService.loadUserByUsername(rawEmail);
        } catch (UsernameNotFoundException ex) {
            return RefreshMeCurrentSession.Result.userNotFound(rawEmail);
        }

        Long userId = securityUser.getUser().getId();

        SessionInfo sessionInfo = securityUser.getSessionInfo();
        String rawDeviceId = sessionInfo.getDeviceId();
        SessionDeviceId sessionDeviceId = SessionDeviceId.of(rawDeviceId);

        Optional<Session> session = sessionRepository.getSessionByUserIdAndDeviceId(userId, sessionDeviceId);

        if (session.isEmpty() && !sessionInfo.isInWhitelist()) {
            return RefreshMeCurrentSession.Result.sessionNotFound(sessionDeviceId.getDeviceId());
        }

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        TokenDto tokenDto = TokenDto.of(accessToken);

        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser)
                .exceptionally(ex -> {
                    log.error("Error saving session", ex);
                    return null;
                });

        return RefreshMeCurrentSession.Result.success(tokenDto);
    }

    private Optional<Session> getSessionBySecurityUser(SecurityUser securityUser) {
        Long userId = securityUser.getUser().getId();
        String rawDeviceId = securityUser.getSessionInfo().getDeviceId();
        SessionDeviceId sessionDeviceId = SessionDeviceId.of(rawDeviceId);
        return sessionRepository.getSessionByUserIdAndDeviceId(userId, sessionDeviceId);

    }
}
