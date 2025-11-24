package com.surofu.exporteru.core.model.product.deliveryMethodDetails;

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
@Table(name = "product_delivery_method_details")
public final class ProductDeliveryMethodDetails implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Embedded
  private ProductDeliveryMethodDetailsName name;

  @Embedded
  private ProductDeliveryMethodDetailsValue value;

  @Embedded
  private ProductDeliveryMethodDetailsCreationDate creationDate;

  @Embedded
  private ProductDeliveryMethodDetailsLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (!(o instanceof ProductDeliveryMethodDetails productDeliveryMethodDetails)) {
          return false;
      }
    return Objects.equals(name, productDeliveryMethodDetails.name)
        && Objects.equals(value, productDeliveryMethodDetails.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
