package com.surofu.exporteru.core.model.advertisement;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AdvertisementThirdText implements Serializable {

    @Column(name = "third_text")
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "third_text_translations", columnDefinition = "hstore")
    private Map<String, String> translations = new HashMap<>();

    private AdvertisementThirdText(String text) {
        if (text != null && text.length() > 255) {
            throw new LocalizedValidationException("validation.third_title.max_length");
        }

        this.value = text;
    }

    public String getLocalizedValue(Locale locale) {
        if (translations == null || translations.isEmpty()) {
            return Objects.requireNonNullElse(value, "");
        }
        return Objects.requireNonNullElse(translations.get(locale.getLanguage()), Objects.requireNonNullElse(value, ""));
    }

    public static AdvertisementThirdText of(String text) {
        return new AdvertisementThirdText(text);
    }

    @Override
    public String toString() {
        return value;
    }
}
