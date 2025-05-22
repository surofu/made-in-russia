package com.surofu.madeinrussia.core.model.product.productReview.productReviewMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductReviewMediaCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime creationDate;

    private ProductReviewMediaCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public static ProductReviewMediaCreationDate of(ZonedDateTime date) {
        return new ProductReviewMediaCreationDate(date);
    }

    @Override
    public String toString() {
        return creationDate.toString();
    }
}
