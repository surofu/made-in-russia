package com.surofu.exporteru.application.model.session;

import com.surofu.exporteru.core.model.session.SessionDeviceId;
import com.surofu.exporteru.core.model.session.SessionIpAddress;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class SessionInfo implements Serializable {
    private UserAgent userAgent;

    private SessionIpAddress ipAddress;

    private SessionDeviceId deviceId;

    private String sessionKey;

    public static SessionInfo of(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        String xInternalRequestHeader = request.getHeader("X-Internal-Request");

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        String ipAddress = getClientIpAddressFromHttpRequest(request);
        String deviceId = generateDeviceId(userAgent);

        return SessionInfo.builder()
                .userAgent(userAgent)
                .ipAddress(new SessionIpAddress(ipAddress))
                .deviceId(new SessionDeviceId(deviceId))
                .sessionKey(xInternalRequestHeader)
                .build();
    }

    // ---------- Private ---------- //

    private static String getClientIpAddressFromHttpRequest(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // Если заголовков нет, берем стандартный IP
        }
        return ip.split(",")[0].trim();
    }

    private static String generateDeviceId(UserAgent userAgent) {
        String deviceIdString = buildDeviceHashStringFromUserAgent(userAgent);

        try {
            return hashStringWithSha256(deviceIdString);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            return hashStringWithUuid(deviceIdString);
        }
    }

    private static String buildDeviceHashStringFromUserAgent(UserAgent userAgent) {
     return userAgent.getOperatingSystem().getName() +
             userAgent.getOperatingSystem().getDeviceType().getName() +
             userAgent.getOperatingSystem().getManufacturer().getId() +
             userAgent.getOperatingSystem().getManufacturer().getName() +
             userAgent.getBrowser().getId() +
             userAgent.getBrowser().getName() +
             userAgent.getBrowser().getBrowserType().getName() +
             userAgent.getBrowser().getGroup() +
             userAgent.getBrowser().getManufacturer().getId() +
             userAgent.getBrowser().getManufacturer().getName();
    }

    private static String hashStringWithSha256(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        return new String(encodedHash, StandardCharsets.UTF_8);
    }

    private static String hashStringWithUuid(String str) {
        return UUID.nameUUIDFromBytes(str.getBytes()).toString();
    }
}
