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
public final class FaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false)
    private String value;

    private FaqAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.faq.answer.empty");
        }

        if (answer.length() > 20_000) {
            throw new LocalizedValidationException("validation.faq.answer.max_length");
        }

        this.value = answer;
    }

    public static FaqAnswer of(String answer) {
        return new FaqAnswer(answer);
    }

    @Override
    public String toString() {
        return value;
    }
}
