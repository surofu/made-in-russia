package com.surofu.madeinrussia.application.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class EmailVerificationUtils {
    private final int CODE_LENGTH = 4;

    public String generateVerificationCode() {
        StringBuilder verificationCode = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomInt = random.nextInt(10);
            verificationCode.append(randomInt);
        }

        log.info("Generated verification code: {}", verificationCode);
        return verificationCode.toString();
    }
}
