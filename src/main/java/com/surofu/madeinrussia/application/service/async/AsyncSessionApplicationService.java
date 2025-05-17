package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.session.*;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.repository.SessionWithUserRepository;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
public class AsyncSessionApplicationService {
    private final SessionRepository sessionRepository;
    private final SessionWithUserRepository sessionWithUserRepository;

    @Async
    @Transactional
    public CompletableFuture<Void> saveOrUpdateSessionFromHttpRequest(SecurityUser securityUser) throws CompletionException {
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
        SessionLastLoginDate sessionLastLoginDate = SessionLastLoginDate.of(dateNow);

        SessionWithUser sessionWithUser = sessionWithUserRepository
                .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
                .orElse(new SessionWithUser());

        sessionWithUser.setUser(securityUser.getUser());
        sessionWithUser.setDeviceId(sessionDeviceId);
        sessionWithUser.setDeviceType(sessionDeviceType);
        sessionWithUser.setBrowser(sessionBrowser);
        sessionWithUser.setOs(sessionOs);
        sessionWithUser.setIpAddress(sessionIpAddress);
        sessionWithUser.setLastModificationDate(sessionLastModificationDate);
        sessionWithUser.setLastLoginDate(sessionLastLoginDate);

        try {
            sessionWithUserRepository.saveOrUpdate(sessionWithUser);
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> removeSessionByUserIdAndDeviceId(Long userId, SessionDeviceId sessionDeviceId) throws CompletionException {
        try {
            sessionRepository.deleteSessionByUserIdAndDeviceId(userId, sessionDeviceId);
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }

        return CompletableFuture.completedFuture(null);
    }
}
