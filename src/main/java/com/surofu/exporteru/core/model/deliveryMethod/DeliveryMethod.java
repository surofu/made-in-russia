package com.surofu.exporteru.core.model.deliveryMethod;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_methods")
public final class DeliveryMethod implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private DeliveryMethodName name;

  @Embedded
  private DeliveryMethodCreationDate creationDate;

  @Embedded
  private DeliveryMethodLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeliveryMethod deliveryMethod)) {
      return false;
    }
    return Objects.equals(name, deliveryMethod.name);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
