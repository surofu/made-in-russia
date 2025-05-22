package com.surofu.madeinrussia.core.model.product.productMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductMediaPosition implements Serializable {

    @Column(nullable = false)
    private Integer position;

    private ProductMediaPosition(Integer position) {
        this.position = position;
    }

    public static ProductMediaPosition of(Integer position) {
        return new ProductMediaPosition(position);
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
