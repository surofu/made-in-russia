package com.surofu.madeinrussia.core.model.faq;

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
public final class FaqQuestion implements Serializable {

    @Column(name = "question", nullable = false)
    private String value;

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

    @Override
    public String toString() {
        return value;
    }
}
