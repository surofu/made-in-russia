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
    @Column(name = "title")
    private String value;

    private ProductTitle(String value) {
        this.value = value;
    }

    public static ProductTitle of(String value) {
        return new ProductTitle(value);
    }
}
