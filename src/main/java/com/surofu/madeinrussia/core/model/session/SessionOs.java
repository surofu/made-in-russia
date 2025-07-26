package com.surofu.madeinrussia.core.model.session;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SessionOs implements Serializable {

    @Column(name = "os", nullable = false, updatable = false)
    private String value;

    private SessionOs(String os) {
        if (os == null || os.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.session.os.empty");
        }

        if (os.length() > 255) {
            throw new LocalizedValidationException("validation.session.os.max_length");
        }

        this.value = os;
    }

    public static SessionOs of(String os) {
        return new SessionOs(os);
    }

    @Override
    public String toString() {
        return value;
    }
}
