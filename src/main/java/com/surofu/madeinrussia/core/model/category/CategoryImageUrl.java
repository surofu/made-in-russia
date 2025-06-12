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
public final class CategoryImageUrl implements Serializable {

    @Column(name = "image_url")
    private String value;

    private CategoryImageUrl(String url) {
        if (url.length() > 255) {
            throw new IllegalArgumentException("Ссылка на изображение категории не может быть больше 20,000 символов");
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
