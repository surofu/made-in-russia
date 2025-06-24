package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductDescription implements Serializable {

    @Column(name = "main_description", nullable = false, columnDefinition = "text")
    private String mainDescription;

    @Column(name = "further_description", nullable = false, columnDefinition = "text")
    private String furtherDescription;

    private ProductDescription(String mainDescription, String furtherDescription) {
        if (mainDescription == null || mainDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Главное описание не может быть пустым");
        }

        if (furtherDescription == null || furtherDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Второстепенное описание не может быть пустым");
        }

        if (mainDescription.length() >= 50_000) {
            throw new IllegalArgumentException("Главное описание не может быть больше 50,000 символов");
        }

        if (furtherDescription.length() >= 20_000) {
            throw new IllegalArgumentException("Второстепенное описание не может быть больше 20,000 символов");
        }

        this.mainDescription = mainDescription;
        this.furtherDescription = furtherDescription;
    }

    public static ProductDescription of(String mainDescription, String furtherDescription) {
        return new ProductDescription(mainDescription, furtherDescription);
    }

    @Override
    public String toString() {
        return mainDescription + " - " + furtherDescription;
    }
}
