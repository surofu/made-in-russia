package com.surofu.madeinrussia.core.model.product.faq;

import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
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
public final class ProductFaqQuestion implements Serializable {

    @Column(name = "question", nullable = false, columnDefinition = "text")
    private String value;

    @ColumnTransformer(write = "?::hstore")
    @Column(name = "question_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private ProductFaqQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Вопрос не может быть пустым");
        }

        if (question.length() >= 20_000) {
            throw new IllegalArgumentException("Вопрос не может быть больше 20,000 символов");
        }

        this.value = question;
    }

    public static ProductFaqQuestion of(String question) {
        return new ProductFaqQuestion(question);
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
