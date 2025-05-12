package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.session.*;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class AsyncSessionApplicationService {
    private final SessionRepository sessionRepository;

    @Async
    public CompletableFuture<Void> saveOrUpdateSessionFromHttpRequest(String userAgentString, String ipAddress, User user) {
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);

        Browser browser = userAgent.getBrowser();
        String browserName = browser.getName();

        OperatingSystem os = userAgent.getOperatingSystem();
        String osName = os.getName();

        String deviceType = os.getDeviceType().getName();
        String deviceId = generateDeviceId(ipAddress, userAgentString);

        Session session = sessionRepository
                .getSessionByDeviceId(SessionDeviceId.of(deviceId))
                .orElseGet(Session::new);

        session.setUser(user);
        session.setDeviceId(SessionDeviceId.of(deviceId));
        session.setDeviceType(SessionDeviceType.of(deviceType));
        session.setBrowser(SessionBrowser.of(browserName));
        session.setOs(SessionOs.of(osName));
        session.setIpAddress(SessionIpAddress.of(ipAddress));

        sessionRepository.saveOrUpdate(session);

        return CompletableFuture.completedFuture(null);
    }

    private String generateDeviceId(String ipAddress, String userAgentString) {
        String combined = ipAddress + userAgentString;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes());

            // Преобразуем байты в HEX-строку
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Если SHA-256 не поддерживается, используем UUID
            return generateDeviceIdByUUID(userAgentString, ipAddress);
        }
    }

    private String generateDeviceIdByUUID(String userAgent, String ipAddress) {
        return UUID.nameUUIDFromBytes((ipAddress + userAgent).getBytes()).toString();
    }
}
