package com.surofu.exporteru.application.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class AuthUtils {
    public static String generateVerificationCode() {
        StringBuilder verificationCode = new StringBuilder();
        Random random = new Random();

        int CODE_LENGTH = 4;
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomInt = random.nextInt(10);
            verificationCode.append(randomInt);
        }

        log.info("Generated verification code: {}", verificationCode);
        return verificationCode.toString();
    }
}
