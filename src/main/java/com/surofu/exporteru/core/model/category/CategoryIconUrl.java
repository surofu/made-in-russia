package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryIconUrl implements Serializable {

    @Column(name = "icon_url")
    private String value;

    private CategoryIconUrl(String url) {
        if (url != null && url.length() > 20_000) {
            throw new LocalizedValidationException("validation.category.image_url.max_length");
        }

        this.value = url;
    }

    public static CategoryIconUrl of(String url) {
        return new CategoryIconUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
