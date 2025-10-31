package com.surofu.exporteru.core.model.faq;

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
public final class FaqQuestion implements Serializable {

    @Column(name = "question", nullable = false)
    private String value;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "question_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private FaqQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.faq.question.empty");
        }

        if (question.length() > 20_000) {
            throw new LocalizedValidationException("validation.faq.question.max_length");
        }

        this.value = question;
    }

    public static FaqQuestion of(String question) {
        return new FaqQuestion(question);
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
