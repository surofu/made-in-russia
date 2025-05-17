package com.surofu.madeinrussia.application.model.session;

import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.session.SessionIpAddress;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
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
                .ipAddress(SessionIpAddress.of(ipAddress))
                .deviceId(SessionDeviceId.of(deviceId))
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

        String deviceIdString = userAgent.getOperatingSystem().getName() +
                userAgent.getOperatingSystem().getDeviceType().getName() +
                userAgent.getOperatingSystem().getManufacturer().getId() +
                userAgent.getOperatingSystem().getManufacturer().getName() +
                userAgent.getBrowser().getId() +
                userAgent.getBrowser().getName() +
                userAgent.getBrowser().getBrowserType().getName() +
                userAgent.getBrowser().getGroup() +
                userAgent.getBrowser().getManufacturer().getId() +
                userAgent.getBrowser().getManufacturer().getName();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(deviceIdString.getBytes());

            // Преобразуем байты в HEX-строку
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.nameUUIDFromBytes(deviceIdString.getBytes()).toString();
        }
    }
}
