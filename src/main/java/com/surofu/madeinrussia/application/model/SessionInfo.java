package com.surofu.madeinrussia.application.model;

import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class SessionInfo implements Serializable {

    private UserAgent userAgent;

    private String ipAddress;

    private String deviceId;

    public static SessionInfo of(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        String ipAddress = getClientIpAddressFromHttpRequest(request);
        String deviceId = getDeviceIdFrom(userAgentString, ipAddress);

        return SessionInfo.builder()
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .deviceId(deviceId)
                .build();
    }

    public static boolean compareDevices(SessionInfo firstSessionInfo, SessionInfo secondSessionInfo) {
        String firstDeviceId = firstSessionInfo.getDeviceId();
        String secondDeviceId = secondSessionInfo.getDeviceId();

        return firstDeviceId.equals(secondDeviceId);
    }

    public static boolean compareDevices(HttpServletRequest firstRequest, HttpServletRequest secondRequest) {
        SessionInfo firstSessionInfo = SessionInfo.of(firstRequest);
        SessionInfo secondSessionInfo = SessionInfo.of(secondRequest);

        return compareDevices(firstSessionInfo, secondSessionInfo);
    }

    public boolean compareDevice(SessionInfo sessionInfo) {
        String firstDeviceId = sessionInfo.getDeviceId();
        return firstDeviceId.equals(deviceId);
    }

    public boolean compareDevice(HttpServletRequest request) {
        SessionInfo firstSessionInfo = SessionInfo.of(request);
        return compareDevice(firstSessionInfo);
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

    private static String getDeviceIdFrom(String userAgentString, String ipAddress) {
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
            return UUID.nameUUIDFromBytes(combined.getBytes()).toString();
        }
    }
}
