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
public final class ProductArticleCode implements Serializable {

    @Column(nullable = false, unique = true, updatable = false)
    private String articleCode;

    private ProductArticleCode(String articleCode) {
        this.articleCode = articleCode;
    }

    public static ProductArticleCode of(String code) {
        return new ProductArticleCode(code);
    }

    @Override
    public String toString() {
        return articleCode;
    }
}
