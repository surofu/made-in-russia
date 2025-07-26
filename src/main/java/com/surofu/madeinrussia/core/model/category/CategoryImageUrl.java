package com.surofu.madeinrussia.core.model.category;

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
public final class CategoryImageUrl implements Serializable {

    @Column(name = "image_url")
    private String value;

    private CategoryImageUrl(String url) {
        if (url != null && url.length() > 20_000) {
            throw new LocalizedValidationException("validation.category.image_url.max_length");
        }

        this.value = url;
    }

    public static CategoryImageUrl of(String url) {
        return new CategoryImageUrl(url);
    }

    @Override
    public String toString() {
        return value;
    }
}
