package com.surofu.exporteru.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductArticleCode implements Serializable {

    @Transient
    private static final Pattern ARTICLE_CODE_PATTERN = Pattern.compile("^[A-Za-z]{4}-[0-9]{4}$");

    @Column(name = "article_code", nullable = false, unique = true, updatable = false)
    private String value;

    private ProductArticleCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Артикул товара не может быть пустым");
        }

        if (!ARTICLE_CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Артикул товара должен соответствовать выражению ^[A-Za-z]{4}-[0-9]{4}$");
        }

        this.value = code;
    }

    public static ProductArticleCode of(String code) {
        return new ProductArticleCode(code);
    }

    @Override
    public String toString() {
        return value;
    }
}
