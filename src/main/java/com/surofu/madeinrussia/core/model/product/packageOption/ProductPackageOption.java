package com.surofu.madeinrussia.core.model.product.packageOption;

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
@Table(name = "product_package_options")
public final class ProductPackageOption implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Embedded
    private ProductPackageOptionName name;

    @Embedded
    private ProductPackageOptionPrice price;

    @Embedded
    private ProductPackageOptionPriceUnit priceUnit;

    @Embedded
    private ProductPackageOptionCreationDate creationDate;

    @Embedded
    private ProductPackageOptionLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPackageOption)) return false;
        return id != null && id.equals(((ProductPackageOption) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
