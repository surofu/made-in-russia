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

    @Column(nullable = false)
    private String mainDescription;

    @Column(nullable = false)
    private String furtherDescription;

    @Column(nullable = false)
    private String summaryDescription;

    private ProductDescription(String mainDescription, String furtherDescription, String summaryDescription) {
        this.mainDescription = mainDescription;
        this.furtherDescription = furtherDescription;
        this.summaryDescription = summaryDescription;
    }

    public static ProductDescription of(String mainDescription, String furtherDescription, String summaryDescription) {
        return new ProductDescription(mainDescription, furtherDescription, summaryDescription);
    }

    @Override
    public String toString() {
        return mainDescription + " - " + furtherDescription + " - " + summaryDescription;
    }
}
