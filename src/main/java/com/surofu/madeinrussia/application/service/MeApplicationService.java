package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.SessionDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.security.SecurityUser;
import com.surofu.madeinrussia.application.utils.SessionUtils;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.GetMeByJwt;
import com.surofu.madeinrussia.core.service.me.operation.GetMeSessions;
import com.surofu.madeinrussia.core.service.me.operation.UpdateMeAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeApplicationService implements MeService {
    private final SessionRepository sessionRepository;
    private final SessionUtils sessionUtils;

    @Override
    public GetMeByJwt.Result getMeByJwt(GetMeByJwt operation) {
        SecurityUser securityUser = operation.getQuery().securityUser();
        User user = securityUser.getUser();
        UserDto userDto = UserDto.of(user);

        SessionDeviceId sessionDeviceId = sessionUtils.getDeviceId(operation.getQuery().userAgent(), operation.getQuery().ipAddress());
        Optional<Session> existingSession = sessionRepository.getSessionByDeviceId(sessionDeviceId);

        if (existingSession.isEmpty()) {
            return GetMeByJwt.Result.sessionWithDeviceNotFound(sessionDeviceId);
        }

        return GetMeByJwt.Result.success(userDto);
    }

    @Override
    public UpdateMeAccessToken.Result updateMeAccessToken(UpdateMeAccessToken operation) {
        return null;
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
}
