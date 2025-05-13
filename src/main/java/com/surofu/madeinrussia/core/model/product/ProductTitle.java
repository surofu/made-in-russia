package com.surofu.madeinrussia.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductTitle implements Serializable {

    @Column(nullable = false)
    private String title;

    private ProductTitle(String title) {
        this.title = title;
    }

    public static ProductTitle of(String title) {
        return new ProductTitle(title);
    }
}
