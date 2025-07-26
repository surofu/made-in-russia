package com.surofu.madeinrussia.core.model.auth;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VerificationCode implements Serializable {

    private String value;

    private VerificationCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.verification_code.empty");
        }

        if (code.length() > 255) {
            throw new LocalizedValidationException("validation.verification_code.max_length");
        }

        this.value = code;
    }

    public static VerificationCode of(String code) {
        return new VerificationCode(code);
    }

    @Override
    public String toString() {
        return value;
    }
}
