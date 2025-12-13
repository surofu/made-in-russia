package com.surofu.exporteru.core.model.deliveryTerm;

import com.surofu.exporteru.core.model.product.Product;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_terms")
public final class DeliveryTerm implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Fetch(FetchMode.SUBSELECT)
  @ManyToMany
  @JoinTable(
      name = "products_delivery_terms",
      joinColumns = @JoinColumn(name = "delivery_term_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id")
  )
  private Set<Product> products = new HashSet<>();

  @Embedded
  private DeliveryTermCode code;

  @Embedded
  private DeliveryTermName name;

  @Embedded
  private DeliveryTermDescription description;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeliveryTerm deliveryTerm)) {
      return false;
    }
    return Objects.equals(code, deliveryTerm.code)
        && Objects.equals(name, deliveryTerm.name)
        && Objects.equals(description, deliveryTerm.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name, description);
  }
}
