package com.surofu.madeinrussia.core.model.product.productVendorDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductVendorDetailsDescription implements Serializable {

    @Column(name = "main_description", nullable = false, columnDefinition = "text")
    private String mainDescription;

    @Column(name = "further_description", columnDefinition = "text")
    private String furtherDescription;

    private ProductVendorDetailsDescription(String mainDescription, String furtherDescription) {
        if (mainDescription == null || mainDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Главнове описание информации о продавце в товаре не может быть пустой");
        }

        this.mainDescription = mainDescription;
        this.furtherDescription = furtherDescription;
    }

    public static ProductVendorDetailsDescription of(String mainDescription, String furtherDescription) {
        return new ProductVendorDetailsDescription(mainDescription, furtherDescription);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", mainDescription, furtherDescription);
    }
}
