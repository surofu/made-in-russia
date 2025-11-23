package com.surofu.exporteru.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
public final class ProductDescription implements Serializable {

    @Column(name = "main_description", nullable = false, columnDefinition = "text")
    private String mainDescription;

    @Column(name = "further_description", columnDefinition = "text")
    private String furtherDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "main_description_translations")
    private Map<String, String> mainDescriptionTranslations = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "further_description_translations")
    private Map<String, String> furtherDescriptionTranslations = new HashMap<>();

    private ProductDescription(String mainDescription, String furtherDescription) {
        if (mainDescription == null || mainDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Главное описание не может быть пустым");
        }

        if (mainDescription.length() >= 50_000) {
            throw new IllegalArgumentException("Главное описание не может быть больше 50,000 символов");
        }

        if (furtherDescription != null && furtherDescription.length() >= 20_000) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDescription productDescription)) return false;
        return Objects.equals(mainDescription, productDescription.mainDescription)
            && Objects.equals(furtherDescription, productDescription.furtherDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainDescription, furtherDescription);
    }
}
