package com.surofu.madeinrussia.core.model.product.productFaq;

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
public final class ProductFaqCreationDate implements Serializable {

    @CreationTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime creationDate;

    private ProductFaqCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public static ProductFaqCreationDate of(ZonedDateTime date) {
        return new ProductFaqCreationDate(date);
    }

    @Override
    public String toString() {
        return creationDate.toString();
    }
}
