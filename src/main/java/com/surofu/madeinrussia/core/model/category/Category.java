package com.surofu.madeinrussia.core.model.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_category_id",
            foreignKey = @ForeignKey(name = "fk_categories_parent_category_id")
    )
    private Category parent;

    @OneToMany(
            mappedBy = "parent",
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Category> children;

    @Embedded
    private CategorySlug slug;

    @Embedded
    private CategoryName name;

    @Embedded
    private CategoryCreationDate creationDate;

    @Embedded
    private CategoryLastModificationDate lastModificationDate;
}
