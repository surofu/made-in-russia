package com.surofu.madeinrussia.application.service.async;

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
