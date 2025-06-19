package com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorFaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false)
    private String value;

    private VendorFaqAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Ответ не может быть пустым");
        }

        if (answer.length() >= 20_000) {
            throw new IllegalArgumentException("Ответ не может быть больше 20,000 символов");
        }

        this.value = answer;
    }

    public static VendorFaqAnswer of(String answer) {
        return new VendorFaqAnswer(answer);
    }

    @Override
    public String toString() {
        return value;
    }
}
