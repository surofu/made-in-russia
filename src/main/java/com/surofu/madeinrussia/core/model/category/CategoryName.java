package com.surofu.madeinrussia.core.model.category;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryName implements Serializable {
    private String name;

    private CategoryName(String name) {
        this.name = name;
    }

    public static CategoryName of(String name) {
        return new CategoryName(name);
    }
}
