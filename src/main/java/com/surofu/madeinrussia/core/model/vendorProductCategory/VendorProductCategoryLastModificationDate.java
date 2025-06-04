package com.surofu.madeinrussia.core.model.vendorProductCategory;

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
public final class VendorProductCategoryCreationDate implements Serializable {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
    private ZonedDateTime value = ZonedDateTime.now();

    private VendorProductCategoryCreationDate(ZonedDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Дата создания категории товаров продавца не может быть пустой");
        }

        this.value = creationDate;
    }

    public static VendorProductCategoryCreationDate of(ZonedDateTime creationDate) {
        return new VendorProductCategoryCreationDate(creationDate);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
