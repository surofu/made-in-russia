package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.session.*;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncSessionApplicationService {
    private final SessionRepository sessionRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> saveOrUpdateSessionFromHttpRequest(SecurityUser securityUser) throws CompletionException {
        try {
            SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
            Session oldSession = sessionRepository
                    .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
                    .orElse(new Session());
            Session session = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);
            sessionRepository.save(session);
        } catch (Exception ex) {
            log.error("Error while saving session: {}", ex.getMessage(), ex);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> removeSessionByUserIdAndDeviceId(Long userId, SessionDeviceId sessionDeviceId) throws CompletionException {
        try {
            sessionRepository.deleteSessionByUserIdAndDeviceId(userId, sessionDeviceId);
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeSessionById(Long sessionId) {
        try {
            sessionRepository.deleteSessionById(sessionId);
        } catch (Exception ex) {
            log.error("Error while removing session: {}", ex.getMessage(), ex);
        }
    }
}
