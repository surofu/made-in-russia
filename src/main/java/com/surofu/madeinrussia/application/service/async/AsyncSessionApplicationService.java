package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.session.*;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
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
            UserAgent userAgent = securityUser.getSessionInfo().getUserAgent();

            SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
            SessionIpAddress sessionIpAddress = securityUser.getSessionInfo().getIpAddress();

            String rawDeviceType = userAgent.getOperatingSystem().getDeviceType().getName();
            SessionDeviceType sessionDeviceType = SessionDeviceType.of(rawDeviceType);

            String rawBrowserName = userAgent.getBrowser().getName();
            SessionBrowser sessionBrowser = SessionBrowser.of(rawBrowserName);

            String rawOsName = userAgent.getOperatingSystem().getName();
            SessionOs sessionOs = SessionOs.of(rawOsName);

            ZonedDateTime dateNow = ZonedDateTime.now();
            SessionLastModificationDate sessionLastModificationDate = SessionLastModificationDate.of(dateNow);
            SessionCreationDate sessionCreationDate = SessionCreationDate.of(dateNow);

            Session session = sessionRepository
                    .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
                    .orElse(new Session());

            session.setUser(securityUser.getUser());
            session.setDeviceId(sessionDeviceId);
            session.setDeviceType(sessionDeviceType);
            session.setBrowser(sessionBrowser);
            session.setOs(sessionOs);
            session.setIpAddress(sessionIpAddress);
            session.setCreationDate(sessionCreationDate);
            session.setLastModificationDate(sessionLastModificationDate);

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
}
