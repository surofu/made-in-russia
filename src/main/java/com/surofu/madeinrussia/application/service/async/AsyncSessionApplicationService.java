package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.application.utils.SessionUtils;
import com.surofu.madeinrussia.core.model.session.*;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class AsyncSessionApplicationService {
    private final SessionRepository sessionRepository;
    private final SessionUtils sessionUtils;

    @Async
    public CompletableFuture<Void> saveOrUpdateSessionFromHttpRequest(String userAgentString, String ipAddress, User user) {
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);

        Browser browser = userAgent.getBrowser();
        String browserName = browser.getName();

        OperatingSystem os = userAgent.getOperatingSystem();
        String osName = os.getName();

        String deviceType = os.getDeviceType().getName();
        SessionDeviceId sessionDeviceId = sessionUtils.generateDeviceId(userAgentString, ipAddress);

        ZonedDateTime dateNow = ZonedDateTime.now();

        SessionLastModificationDate sessionLastModificationDate = SessionLastModificationDate.of(dateNow);
        SessionLastLoginDate sessionLastLoginDate = SessionLastLoginDate.of(dateNow);

        Session session = sessionRepository
                .getSessionByDeviceId(sessionDeviceId)
                .orElseGet(Session::new);

        session.setUser(user);
        session.setDeviceId(sessionDeviceId);
        session.setDeviceType(SessionDeviceType.of(deviceType));
        session.setBrowser(SessionBrowser.of(browserName));
        session.setOs(SessionOs.of(osName));
        session.setIpAddress(SessionIpAddress.of(ipAddress));
        session.setLastModificationDate(sessionLastModificationDate);
        session.setLastLoginDate(sessionLastLoginDate);

        sessionRepository.saveOrUpdate(session);

        return CompletableFuture.completedFuture(null);
    }
}
