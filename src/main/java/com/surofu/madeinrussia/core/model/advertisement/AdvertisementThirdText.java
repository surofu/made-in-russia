package com.surofu.madeinrussia.core.model.advertisement;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import com.surofu.madeinrussia.application.utils.HstoreParser;
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
public final class AdvertisementThirdText implements Serializable {

    @Column(name = "third_text")
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "third_text_translations", columnDefinition = "hstore")
    private String translations;

    private AdvertisementThirdText(String text) {
        if (text != null && text.length() > 255) {
            throw new LocalizedValidationException("validation.third_title.max_length");
        }

        this.value = text;
    }

    public static AdvertisementThirdText of(String text) {
        return new AdvertisementThirdText(text);
    }

    public HstoreTranslationDto getTranslations() {
        return HstoreParser.fromString(translations);
    }

    public void setTranslations(HstoreTranslationDto translations) {
        this.translations = HstoreParser.toString(translations);
    }

    @Override
    public String toString() {
        return value;
    }
}
