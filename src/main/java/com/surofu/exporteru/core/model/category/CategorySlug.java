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
public final class CategorySlug implements Serializable {

    @Column(name = "slug", unique = true, nullable = false)
    private String value;

    private CategorySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.category.slug.empty");
        }

        if (slug.length() > 255) {
            throw new LocalizedValidationException("validation.category.slug.max_length");
        }

        this.value = slug;
    }

    public static CategorySlug of(String slug) {
        return new CategorySlug(slug);
    }

    public static CategorySlug of(String slug, int level) {
        return new CategorySlug("l%d_%s".formatted(level, slug));
    }

    @Override
    public String toString() {
        return value;
    }
}
