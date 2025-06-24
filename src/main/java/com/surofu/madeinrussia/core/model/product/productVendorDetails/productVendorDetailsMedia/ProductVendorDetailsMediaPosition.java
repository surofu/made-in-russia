package com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductVendorDetailsMediaPosition implements Serializable {

    @Column(name = "position", nullable = false)
    private Integer value;

    private ProductVendorDetailsMediaPosition(Integer position) {
        if (position == null) {
            throw new IllegalArgumentException("Позиция медиа в информации о продавце в товаре не может быть пустой");
        }

        if (position < 0) {
            throw new IllegalArgumentException("Позиция медиа в информации о продавце в товаре не может быть отрицательной");
        }

        this.value = position;
    }

    public static ProductVendorDetailsMediaPosition of(Integer position) {
        return new ProductVendorDetailsMediaPosition(position);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
