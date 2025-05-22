package com.surofu.madeinrussia.core.model.product.productMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductMediaCreationDate implements Serializable {

    @Column(nullable = false)
    private ZonedDateTime creationDate;

    private ProductMediaCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public static ProductMediaCreationDate of(ZonedDateTime date) {
        return new ProductMediaCreationDate(date);
    }

    @Override
    public String toString() {
        return creationDate.toString();
    }
}
