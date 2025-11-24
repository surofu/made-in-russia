package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.core.model.product.Product;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
      if (this == o) {
          return true;
      }
      if (!(o instanceof ProductPrice productPrice)) {
          return false;
      }
    return Objects.equals(id, productPrice.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
