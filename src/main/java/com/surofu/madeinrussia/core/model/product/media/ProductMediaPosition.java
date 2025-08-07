package com.surofu.madeinrussia.core.model.product.media;

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

    @Column(name = "position", nullable = false, columnDefinition = "int default 0")
    private Integer value = 0;

    private ProductMediaPosition(Integer position) {
        if (position == null) {
            throw new IllegalArgumentException("Позиция медиа не может быть пустой");
        }

        if (position < 0) {
            throw new IllegalArgumentException("Позиция медиа не может быть отрицательной");
        }

        this.value = position;
    }

    public static ProductMediaPosition of(Integer position) {
        return new ProductMediaPosition(position);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
