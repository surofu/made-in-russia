package com.surofu.madeinrussia.core.model.product.productPrice;

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
@Table(name = "product_prices")
public final class ProductPrice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Embedded
    private ProductPriceQuantityRange quantityRange;

    @Embedded
    private ProductPriceCurrency currency;

    @Embedded
    private ProductPriceUnit unit;

    @Embedded
    private ProductPriceOriginalPrice originalPrice;

    @Embedded
    private ProductPriceDiscount discount;

    @Embedded
    private ProductPriceDiscountedPrice discountedPrice;

    @Embedded
    private ProductPriceMinimumOrderQuantity minimumOrderQuantity;

    @Embedded
    private ProductPriceDiscountExpiryDate expiryDate;

    @Embedded
    private ProductPriceCreationDate creationDate;

    @Embedded
    private ProductPriceLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPrice)) return false;
        return id != null && id.equals(((ProductPrice) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
