package com.surofu.madeinrussia.core.model.vendorDetails.faq;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorFaqQuestion implements Serializable {

    @Column(name = "question", nullable = false)
    private String value;

    private VendorFaqQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Вопрос не может быть пустым");
        }

        if (question.length() >= 20_000) {
            throw new IllegalArgumentException("Вопрос не может быть больше 20,000 символов");
        }

        this.value = question;
    }

    public static VendorFaqQuestion of(String question) {
        return new VendorFaqQuestion(question);
    }

    @Override
    public String toString() {
        return value;
    }
}
