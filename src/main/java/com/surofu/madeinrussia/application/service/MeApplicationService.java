package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.TokenDto;
import com.surofu.madeinrussia.application.dto.SessionDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.security.SecurityUser;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.application.utils.SessionUtils;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.GetMe;
import com.surofu.madeinrussia.core.service.me.operation.GetMeCurrentSession;
import com.surofu.madeinrussia.core.service.me.operation.GetMeSessions;
import com.surofu.madeinrussia.core.service.me.operation.RefreshMeCurrentSession;
import com.surofu.madeinrussia.core.service.user.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeApplicationService implements MeService {
    private final SessionRepository sessionRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final SessionUtils sessionUtils;
    private final AsyncSessionApplicationService asyncSessionApplicationService;

    @Override
    public GetMe.Result getMeByJwt(GetMe operation) {
        SecurityUser securityUser = operation.getQuery().securityUser();
        Optional<Session> existingSession = securityUser.getSession();

        if (existingSession.isEmpty()) {
            return GetMe.Result.sessionIsEmpty();
        }

        User user = existingSession.get().getUser();
        UserDto userDto = UserDto.of(user);

        return GetMe.Result.success(userDto);
    }

    @Override
    public GetMeSessions.Result getMeSessions(GetMeSessions operation) {
        SecurityUser securityUser = operation.getQuery().securityUser();

        User user = securityUser.getUser();
        Long userId = user.getId();
        List<Session> sessions = sessionRepository.getSessionsByUserId(userId);

        List<SessionDto> sessionDtos = new ArrayList<>(sessions.size());

        for (Session session : sessions) {
            SessionDto sessionDto = SessionDto.of(session);
            sessionDtos.add(sessionDto);
        }

        return GetMeSessions.Result.success(sessionDtos);
    }

    @Override
    public GetMeCurrentSession.Result getMeCurrentSession(GetMeCurrentSession operation) {
        Optional<Session> session = operation.getQuery().securityUser().getSession();
        Optional<SessionDto> sessionDto = session.map(SessionDto::of);

        if (sessionDto.isEmpty()) {
            return GetMeCurrentSession.Result.sessionIsEmpty();
        }

        return GetMeCurrentSession.Result.success(sessionDto.get());
    }

    @Override
    public RefreshMeCurrentSession.Result refreshMeCurrentSession(RefreshMeCurrentSession operation) {
        String refreshToken = operation.getCommand().refreshToken();
        String userEmail;

        try {
            userEmail = jwtUtils.extractEmailFromRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException ex) {
            return RefreshMeCurrentSession.Result.invalidRefreshToken(refreshToken, ex);
        }

        SecurityUser securityUser;

        try {
            securityUser = (SecurityUser) userService.loadUserByUsername(userEmail);
        } catch (UsernameNotFoundException ex) {
            return RefreshMeCurrentSession.Result.userNotFound(userEmail);
        }

        String userAgent = operation.getUserAgent();
        String ipAddress = operation.getIpAddress();

        SessionDeviceId sessionDeviceId = sessionUtils.getDeviceId(userAgent, ipAddress);
        Optional<Session> existingSession = sessionRepository.getSessionByDeviceId(sessionDeviceId);

        if (existingSession.isEmpty()) {
            return RefreshMeCurrentSession.Result.userNotFound(userEmail);
        }

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        TokenDto tokenDto = TokenDto.of(accessToken);

        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(userAgent, ipAddress, securityUser);

        return RefreshMeCurrentSession.Result.success(tokenDto);
    }
}
