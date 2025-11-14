package com.surofu.exporteru.core.model.product.review;

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
public final class ProductReviewContent implements Serializable {

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String value;

    // TODO: ProductReviewContent Translation. Hstore -> Jsonb
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "content_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private ProductReviewContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.product.review.content.empty");
        }

        if (content.length() >= 10_000) {
            throw new LocalizedValidationException("validation.product.review.content.max_length");
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
