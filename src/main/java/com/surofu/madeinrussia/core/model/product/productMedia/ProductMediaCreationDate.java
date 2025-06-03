package com.surofu.madeinrussia.core.model.product.productMedia;

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
public final class ProductMediaCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private ProductMediaCreationDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Дата создания медиа не может быть пустой");
        }

        this.value = date;
    }

    public static ProductMediaCreationDate of(ZonedDateTime date) {
        return new ProductMediaCreationDate(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
