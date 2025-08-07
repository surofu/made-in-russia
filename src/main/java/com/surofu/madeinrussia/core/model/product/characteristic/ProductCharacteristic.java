package com.surofu.madeinrussia.core.model.product.characteristic;

import com.surofu.madeinrussia.core.model.product.Product;
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
        name = "product_characteristics",
        indexes = {
                @Index(
                        name = "idx_product_characteristics_product_id",
                        columnList = "product_id"
                )
        }
)
public final class ProductCharacteristic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_characteristics_product_id")
    )
    private Product product;

    @Embedded
    private ProductCharacteristicName name;

    @Embedded
    private ProductCharacteristicValue value;

    @Embedded
    private ProductCharacteristicCreationDate creationDate;

    @Embedded
    private ProductCharacteristicLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCharacteristic)) return false;
        return id != null && id.equals(((ProductCharacteristic)o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
