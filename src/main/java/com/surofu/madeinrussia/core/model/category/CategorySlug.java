package com.surofu.madeinrussia.core.model.category;

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
            throw new IllegalArgumentException("Поисковое название категории не может быть пустым");
        }

        if (slug.length() > 255) {
            throw new IllegalArgumentException("Поисковое название категории не может быть больше 255 символов");
        }

        this.value = slug;
    }

    public static CategorySlug of(String slug) {
        return new CategorySlug(slug);
    }

    @Override
    public String toString() {
        return value;
    }
}
