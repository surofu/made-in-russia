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
public final class ProductMediaLastModificationDate implements Serializable {

    @Column(nullable = false)
    private ZonedDateTime lastModificationDate;

    private ProductMediaLastModificationDate(ZonedDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public static ProductMediaLastModificationDate of(ZonedDateTime date) {
        return new ProductMediaLastModificationDate(date);
    }

    @Override
    public String toString() {
        return lastModificationDate.toString();
    }
}
