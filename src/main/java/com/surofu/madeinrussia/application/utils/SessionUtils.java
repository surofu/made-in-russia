package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class SessionUtils {

    public SessionDeviceId getDeviceId(String userAgent, String ipAddress) {
        return generateDeviceId(userAgent, ipAddress);
    }

    public SessionDeviceId generateDeviceId(String userAgent, String ipAddress) {
        String combined = ipAddress + userAgent;

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

            String deviceId = hexString.toString();
            return SessionDeviceId.of(deviceId);
        } catch (NoSuchAlgorithmException e) {
            return generateDeviceIdByUUID(userAgent, ipAddress);
        }
    }

    public SessionDeviceId generateDeviceIdByUUID(String userAgent, String ipAddress) {
        String deviceId = UUID.nameUUIDFromBytes((ipAddress + userAgent).getBytes()).toString();
        return SessionDeviceId.of(deviceId);
    }
}
