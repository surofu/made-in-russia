package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.exception.LocalizedValidationException;
import com.surofu.exporteru.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserLogin implements Serializable {

    @Column(name = "login", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "login_transliteration")
    private String transliteration;

    private UserLogin(String login) {
        this.value = login;
    }

    public static UserLogin of(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.login.empty");
        }

        if (login.length() < 2) {
            throw new LocalizedValidationException("validation.login.min_length");
        }

        if (login.length() > 255) {
            throw new LocalizedValidationException("validation.login.max_length");
        }

        return new UserLogin(login);
    }

    public HstoreTranslationDto getTransliteration() {
        if (transliteration == null) {
            return null;
        }

        return HstoreParser.fromString(transliteration);
    }

    public void setTransliteration(HstoreTranslationDto transliteration) {
        if (transliteration == null) {
            this.transliteration = null;
        } else {
            this.transliteration = HstoreParser.toString(transliteration);
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value != null && value.equals(((UserLogin) o).value);
    }
}
