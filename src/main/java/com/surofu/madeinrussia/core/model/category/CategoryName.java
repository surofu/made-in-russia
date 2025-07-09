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

    @Column(name = "name", nullable = false)
    private String value;

    @Column(name = "name_en")
    private String valueEn;

    @Column(name = "name_ru")
    private String valueRu;

    @Column(name = "name_zh")
    private String valueZh;

    private CategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Название категории не может быть больше 255 символов");
        }

        this.value = name;
    }

    private CategoryName(String nameEn, String nameRu, String nameZh) {
        if (nameEn == null || nameEn.trim().isEmpty()
                || nameRu == null || nameRu.trim().isEmpty()
                || nameZh == null || nameZh.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }

        if (nameEn.length() > 255 || nameRu.length() > 255 || nameZh.length() > 255) {
            throw new IllegalArgumentException("Название категории не может быть больше 255 символов");
        }

        this.value = nameEn;
        this.valueEn = nameEn;
        this.valueRu = nameRu;
        this.valueZh = nameZh;
    }

    public static CategoryName of(String name) {
        return new CategoryName(name);
    }

    public static CategoryName of(String nameEn, String nameRu, String nameZh) {
        return new CategoryName(nameEn, nameRu, nameZh);
    }

    @Override
    public String toString() {
        return value;
    }
}
