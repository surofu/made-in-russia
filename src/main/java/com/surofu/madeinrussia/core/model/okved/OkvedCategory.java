package com.surofu.madeinrussia.core.model.okved;

import com.surofu.madeinrussia.core.model.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories_okved")
public class OkvedCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_categories_okved_category_id")
    )
    private Category category;

    @Column(name = "okved_id", nullable = false)
    private String okvedId;
}
