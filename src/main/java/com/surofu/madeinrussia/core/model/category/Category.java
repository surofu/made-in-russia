package com.surofu.madeinrussia.core.model.category;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_categories_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_categories_name",
                        columnList = "name"
                )
        }
)
public final class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_category_id",
            foreignKey = @ForeignKey(name = "fk_categories_parent_category_id")
    )
    private Category parent;

    @ToString.Exclude
    @OneToMany(
            mappedBy = "parent",
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private Set<Category> children = new HashSet<>();

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<OkvedCategory> okvedCategories = new HashSet<>();

    @Formula("(select count(*) from categories c where c.parent_category_id = id)")
    private Long childrenCount;

    @Embedded
    private CategorySlug slug;

    @Embedded
    private CategoryName name;

    @Embedded
    private CategoryImageUrl imageUrl;

    @Embedded
    private CategoryCreationDate creationDate;

    @Embedded
    private CategoryLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
