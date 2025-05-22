package com.surofu.madeinrussia.core.model.product.productCharacteristic;

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
public final class ProductCharacteristicCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime creationDate;

    private ProductCharacteristicCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public static ProductCharacteristicCreationDate of(ZonedDateTime date) {
        return new ProductCharacteristicCreationDate(date);
    }

    @Override
    public String toString() {
        return creationDate.toString();
    }
}
