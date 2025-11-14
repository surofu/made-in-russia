package com.surofu.exporteru.core.model.product.faq;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductFaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false, columnDefinition = "text")
    private String value;

    // TODO: ProductFaqAnswer Translation. Hstore -> Jsonb
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "answer_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private ProductFaqAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Ответ не может быть пустым");
        }

        if (answer.length() >= 20_000) {
            throw new IllegalArgumentException("Ответ не может быть больше 20,000 символов");
        }

        this.value = answer;
    }

    public static ProductFaqAnswer of(String answer) {
        return new ProductFaqAnswer(answer);
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
