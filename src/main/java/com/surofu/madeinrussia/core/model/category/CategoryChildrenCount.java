package com.surofu.madeinrussia.core.model.category;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryChildrenCount implements Serializable {

    @Formula("(select count(*) from categories c where c.parent_category_id = id)")
    private Long value;

    private CategoryChildrenCount(Long childrenCount) {
        if (childrenCount < 0) {
            throw new IllegalArgumentException("Количество дочерних сущностей не может быть отрицательным");
        }

        this.value = childrenCount;
    }

    public static CategoryChildrenCount of(Long childrenCount) {
        return new CategoryChildrenCount(childrenCount);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
