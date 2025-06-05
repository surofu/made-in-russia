package com.surofu.madeinrussia.core.model.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.io.Serializable;

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
@BatchSize(size = 20)
public final class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CategoryName name;

    @Embedded
    private CategoryCreationDate creationDate;

    @Embedded
    private CategoryLastModificationDate lastModificationDate;
}
