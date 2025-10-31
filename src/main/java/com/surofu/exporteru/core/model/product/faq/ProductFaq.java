package com.surofu.exporteru.core.model.product.faq;

import com.surofu.exporteru.core.model.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product_faq",
        indexes = {
                @Index(
                        name = "idx_product_faq_product_id",
                        columnList = "product_id"
                )
        }
)
public final class ProductFaq implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_faq_product_id")
    )
    private Product product;

    @Embedded
    private ProductFaqQuestion question;

    @Embedded
    private ProductFaqAnswer answer;

    @Embedded
    private ProductFaqCreationDate creationDate;

    @Embedded
    private ProductFaqLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductFaq)) return false;
        return id != null && id.equals(((ProductFaq) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
