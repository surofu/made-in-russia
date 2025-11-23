package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.core.model.product.Product;
import jakarta.persistence.*;
import java.util.Objects;
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
    @JoinColumn(name = "product_id", nullable = false)
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
    private ProductPriceCreationDate creationDate;

    @Embedded
    private ProductPriceLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPrice productPrice)) return false;
        return Objects.equals(originalPrice, productPrice.originalPrice)
            && Objects.equals(discount, productPrice.discount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalPrice, discount);
    }
}
