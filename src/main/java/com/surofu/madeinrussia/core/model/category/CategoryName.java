package com.surofu.madeinrussia.core.model.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryName implements Serializable {

    @Column(name = "name", unique = true, nullable = false)
    private String value;

    private CategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Название категории не может быть больше 255 символов");
        }

        this.value = name;
    }

    public static CategoryName of(String name) {
        return new CategoryName(name);
    }

    @Override
    public String toString() {
        return value;
    }
}
