package com.surofu.madeinrussia.core.model.product.review;

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
public final class ProductReviewContent implements Serializable {

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "content_translations", nullable = false, columnDefinition = "hstore")
    private String translations;

    private ProductReviewContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Текст отзыва не может быть пустым");
        }

        if (content.length() >= 10_000) {
            throw new IllegalArgumentException("Текст отзыва не может быть больше 10,000 символов");
        }

        this.value = content;
    }

    public static ProductReviewContent of(String content) {
        return new ProductReviewContent(content);
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
