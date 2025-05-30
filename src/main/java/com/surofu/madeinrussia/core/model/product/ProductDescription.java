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

    @Column(nullable = false, columnDefinition = "text")
    private String mainDescription;

    @Column(nullable = false, columnDefinition = "text")
    private String furtherDescription;

    @Column(nullable = false, columnDefinition = "text")
    private String summaryDescription;

    @Column(nullable = false, columnDefinition = "text")
    private String primaryDescription;

    private ProductDescription(String mainDescription, String furtherDescription, String summaryDescription, String primaryDescription) {
        this.mainDescription = mainDescription;
        this.furtherDescription = furtherDescription;
        this.summaryDescription = summaryDescription;
        this.primaryDescription = primaryDescription;
    }

    public static ProductDescription of(String mainDescription, String furtherDescription, String summaryDescription, String primaryDescription) {
        return new ProductDescription(mainDescription, furtherDescription, summaryDescription, primaryDescription);
    }

    @Override
    public String toString() {
        return mainDescription + " - " + furtherDescription + " - " + summaryDescription + " - " + primaryDescription;
    }
}
