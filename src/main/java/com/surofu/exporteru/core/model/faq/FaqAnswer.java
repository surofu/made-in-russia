package com.surofu.exporteru.core.model.faq;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false)
    private String value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_translations")
    private Map<String, String> translations = new HashMap<>();

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
