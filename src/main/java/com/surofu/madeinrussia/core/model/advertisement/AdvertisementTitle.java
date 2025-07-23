package com.surofu.madeinrussia.core.model.advertisement;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
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
public final class AdvertisementTitle implements Serializable {

    @Column(name = "title", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "title_translations", nullable = false, columnDefinition = "hstore")
    private String translations;

    private AdvertisementTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }

        if (title.length() > 255) {
            throw new IllegalArgumentException("Title cannot be longer than 255 characters.");
        }

        this.value = title;
    }

    public static AdvertisementTitle of(String title) {
        return new AdvertisementTitle(title);
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
